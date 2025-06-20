package org.MobiliTECH.service;

import org.MobiliTECH.util.S3Provider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class ImportadorS3 extends LogService {
    private static final String BUCKET_NAME = "mobilitech";
    private static final String FOLDER_IN = "fila/";
    private static final String FOLDER_OUT = "concluido/";

    private final S3Client s3 = new S3Provider().getS3Client();

    public List<String> listarArquivos() {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(FOLDER_IN)
                    .build();

            ListObjectsV2Response response = s3.listObjectsV2(listRequest);

            return response.contents().stream()
                    .map(S3Object::key)
                    .filter(key -> key.toLowerCase().endsWith(".xlsx"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            registrarErro("Erro ao listar arquivos", e.getMessage());
            System.err.println("❌ Erro ao listar arquivos: " + e.getMessage());
            return List.of();
        }
    }

    public InputStream baixarArquivo(String caminho) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(caminho)
                    .build();

            return s3.getObject(request);

        } catch (Exception e) {
            registrarErro("Erro ao baixar arquivo", e.getMessage());
            System.err.println("❌ Erro ao baixar arquivo: " + caminho);
            throw new RuntimeException(e);
        }
    }

    public void moverParaConcluido(String caminhoOriginal) {
        try {
            String novoCaminho = caminhoOriginal.replace(FOLDER_IN, FOLDER_OUT);

            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(BUCKET_NAME)
                    .sourceKey(caminhoOriginal)
                    .destinationBucket(BUCKET_NAME)
                    .destinationKey(novoCaminho)
                    .build();

            s3.copyObject(copyRequest);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(caminhoOriginal)
                    .build();

            s3.deleteObject(deleteRequest);
            registrarInfo("Movimentação de Arquivos S3","Arquivo movido para"+ novoCaminho);
            System.out.println("✅ Arquivo movido para: " + novoCaminho);
        } catch (Exception e) {
            registrarErro("Erro ao mover arquivo S3", "Erro ao mover arquivo: "+ caminhoOriginal);
            System.err.println("❌ Erro ao mover arquivo: " + caminhoOriginal);
        }
    }
}
