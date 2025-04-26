package org.example;

import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseInputStream;

public class ImportadorExcel {

    private static final String BUCKET_NAME = "";
    private static final String FOLDER_IN = "fila/";  
    private static final String FOLDER_OUT = "concluido/";

    public static void main(String[] args) {
        // Listar arquivos .xlsx no bucket S3 na pasta "fila"
        List<String> arquivosXLSX = listarArquivosS3(BUCKET_NAME, FOLDER_IN);

        if (arquivosXLSX.isEmpty()) {
            System.out.println("Nenhum arquivo .xlsx encontrado na pasta 'fila' do bucket.");
            return;
        }

        for (String arquivo : arquivosXLSX) {
            System.out.println("\n🔄 Processando: " + arquivo);
            importarArquivoS3(arquivo);  // Processar o arquivo do S3
        }

        System.out.println("\n✅ Todos os arquivos foram processados.");
    }

    // Listar todos os arquivos .xlsx na pasta 'fila' do bucket S3
    private static List<String> listarArquivosS3(String bucketName, String folder) {
        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)  // Substitua pela sua região
                .credentialsProvider(getCredentialsProvider())
                .build()) {

            // Requisitar a lista de objetos no bucket e na pasta 'fila'
            ListObjectsV2Request listObjects = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folder)  // Só pega arquivos que estão na pasta "fila"
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(listObjects);

            // Filtrar para obter apenas arquivos .xlsx
            return response.contents().stream()
                    .map(S3Object::key)
                    .filter(key -> key.toLowerCase().endsWith(".xlsx"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Erro ao listar arquivos do S3: " + e.getMessage());
            return List.of();  // Retorna uma lista vazia em caso de erro
        }
    }

    // Importar o arquivo do S3 e inserir no banco
    public static void importarArquivoS3(String arquivoS3) {
        String jdbcUrl = System.getenv("DB_HOST");  // Credenciais de banco de dados no ambiente
        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PSWD");

        try (S3Client s3Client = S3Client.builder()
                .region(Region.of("us-east-1"))  // Substitua pela sua região
                .credentialsProvider(getCredentialsProvider())
                .build()) {

            // Cria o pedido para buscar o arquivo do S3
            GetObjectRequest request = GetObjectRequest.builder().bucket(BUCKET_NAME).key(arquivoS3).build();
            // Recebe o ResponseInputStream com o conteúdo do arquivo
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(request);

            // Lendo o arquivo S3 para um InputStream
            ByteArrayInputStream fis = new ByteArrayInputStream(responseInputStream.readAllBytes());
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0); // Lê a primeira aba do Excel
            int count = 0;

            // Começa a leitura a partir da 4ª linha (índice 3)
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try (Connection conn = DriverManager.getConnection(jdbcUrl, usuario, senha);
                     PreparedStatement stmt = conn.prepareStatement(
                             "INSERT INTO transporte (data, grupo, lote, empresa, linha, " +
                                     "passageiros_dinheiro, passageiros_comum_vt, passageiros_comum_m, " +
                                     "passageiros_estudante, passageiros_estudante_mensal, passageiros_vt_mensal, " +
                                     "passageiros_pagantes, passageiros_integracao, passageiros_gratuidade, passageiros_total, " +
                                     "partidas_ponto_inicial, partidas_ponto_final) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        conn.setAutoCommit(false);
                    java.util.Date dataPlanilha = getDate(row, 0);
                    if (dataPlanilha == null) continue;

                    stmt.setDate(1, new Date(dataPlanilha.getTime()));
                    stmt.setString(2, getString(row, 1));
                    stmt.setString(3, getString(row, 2));
                    stmt.setString(4, getString(row, 3));
                    stmt.setString(5, getString(row, 4));

                    for (int j = 6; j <= 14; j++) {
                        stmt.setInt(j, getInt(row, j));
                    }

                    stmt.setInt(15, getInt(row, 15));
                    stmt.setInt(16, getInt(row, 16));
                    stmt.setInt(17, getInt(row, 17));

                    stmt.executeUpdate();
                    count++;
                    conn.commit();
                }
            }

            // Após o processamento, mover o arquivo para a pasta "concluido"
            moverArquivoParaConcluido(s3Client, arquivoS3);

            System.out.println("✅ Inseridos " + count + " registros de " + arquivoS3);

        } catch (Exception e) {
            System.err.println("❌ Erro ao processar " + arquivoS3);
            e.printStackTrace();
        }
    }

    // Mover o arquivo para a pasta 'concluido' no bucket S3
    private static void moverArquivoParaConcluido(S3Client s3Client, String arquivoS3) {
        String novoCaminho = arquivoS3.replace(FOLDER_IN, FOLDER_OUT);

        // Copiar o arquivo para a pasta 'concluido'
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(BUCKET_NAME)
                .sourceKey(arquivoS3)
                .destinationBucket(BUCKET_NAME)
                .destinationKey(novoCaminho)
                .build();
        s3Client.copyObject(copyRequest);

        // Excluir o arquivo original da pasta 'fila'
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(arquivoS3)
                .build();
        s3Client.deleteObject(deleteRequest);

        System.out.println("✅ Arquivo movido para 'concluido/': " + novoCaminho);
    }

    // Obter credenciais da AWS de variáveis de ambiente
    private static StaticCredentialsProvider getCredentialsProvider() {
        String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String sessionToken = System.getenv("AWS_SESSION_TOKEN");

        if (accessKeyId == null || secretAccessKey == null || sessionToken == null) {
            throw new IllegalStateException("As credenciais da AWS ou o session token não foram encontrados nas variáveis de ambiente.");
        }

        // Crie as credenciais temporárias com o session token
        AwsSessionCredentials awsSessionCredentials = AwsSessionCredentials.create(
                accessKeyId, secretAccessKey, sessionToken);

        return StaticCredentialsProvider.create(awsSessionCredentials);
    }

    // Obtém a data de uma célula
    private static Date getDate(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return new Date(cell.getDateCellValue().getTime());
        }
        return null;
    }

    // Obtém o valor da célula como String
    private static String getString(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        return cell != null ? cell.toString().trim() : "";
    }

    // Obtém o valor da célula como inteiro
    private static int getInt(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return 0;
    }
}
