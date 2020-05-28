package com.example.pdfencrypt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;



import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;




import java.net.MalformedURLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * The file created using this example can not be opened, unless
 * you import the private key stored in test.p12 in your certificate store.
 * The password for the p12 file is kspass.
 */










public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //Check for Read permissions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button encryptButton = findViewById(R.id.encryptButton);

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    encryptPDF();
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Need to design this to show only when successful perhaps
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                sendEmail("Cswinney22@gmail.com", outputFile);
                Toast.makeText(getApplicationContext(),"Done!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void encryptPDF() throws IOException, DocumentException, CertificateException, NoSuchAlgorithmException {

        createPdf(DEST);
    }

    final String inputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test.pdf";
    final String outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test-encrypted.pdf";
    public static final String PUBLIC = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/test.cer";
    public static final String DEST = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test-encrypted.pdf";



    public void createPdf(String dest) throws IOException, DocumentException, CertificateException, NoSuchAlgorithmException {

        Security.addProvider(new BouncyCastleProvider());

        PdfReader reader = new PdfReader(inputFile);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

        String certString = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/id_to_pdf.cer";
/*
        //Certificate encryption
        Certificate cert = getPublicCertificate(certString);
        stamper.setEncryption(new Certificate[]{cert},new int[]{PdfWriter.ALLOW_PRINTING}, PdfWriter.STANDARD_ENCRYPTION_128);
*/


        //Password encryption
        stamper.setEncryption("123456".getBytes(), "123456".getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);

        stamper.createXmpMetadata();

        stamper.close();
        reader.close();

    }

    public void sendEmail(String email, String filePath){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT,"This is a test subject");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_TEXT, "Mail with an attachment");

        //Attach a single file
        File file = new File(filePath);
        Uri apkURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file);
        intent.putExtra(Intent.EXTRA_STREAM, apkURI);
        intent.setType("text/plain");

        startActivity(intent);
    }

    public Certificate getPublicCertificate(String path)
            throws IOException, CertificateException {
        FileInputStream is = new FileInputStream(path);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
        return cert;
    }



}

