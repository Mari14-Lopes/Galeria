package gouvea.lopes.mariana.galeria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import gouvea.lopes.mariana.galeria.util.Util;

public class PhotoActivity extends AppCompatActivity {
    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = findViewById(R.id.tbPhoto);
        setSupportActionBar(toolbar);

        //obtendo da Activity a ActionBar padrao(que foi setada acima)
        ActionBar actionBar = getSupportActionBar();
        //habilitando o botao de voltar na ActionBar
        actionBar.setDisplayHomeAsUpEnabled(true);

        //obtendo caminho da foto que foi enviada via intent
        Intent i = getIntent();
        photoPath = i.getStringExtra("photo_path");

        //carregando a foto em um Bitmap
        Bitmap bitmap = Util.getBitmap(photoPath);
        ImageView imPhoto  = findViewById(R.id.imPhoto);
        imPhoto.setImageBitmap(bitmap);
    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_activity_tb, menu);
        return true;
    }
    //metodo para compartilhar a foto
    void sharePhoto(){
        //gerando Uri para a foto
        Uri photoUri = FileProvider.getUriForFile(PhotoActivity.this, "gouvea.lopes.mariana.galeria.fileprovider", new File(photoPath));
        //intent implicito, indicando que queremos enviar algo para qualquer app que seja capaz de aceitar o envio
        Intent i = new Intent(Intent.ACTION_SEND);
        //dizendo qual arquivo estamos querendo compartilhar
        i.putExtra(Intent.EXTRA_STREAM, photoUri);
        //e que tipo de dados o arquivo eh
        i.setType("image/jpeg");
        //executando intent
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            //caso o icone da camera seja clicado a camera sera disparada
            case R.id.opShare:
                sharePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}