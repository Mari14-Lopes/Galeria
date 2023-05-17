package gouvea.lopes.mariana.galeria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gouvea.lopes.mariana.galeria.adapter.MainAdapter;
import gouvea.lopes.mariana.galeria.util.Util;

public class MainActivity extends AppCompatActivity {
    List<String> photos = new ArrayList<>();
    MainAdapter  mainAdapter;
    static  int  RESULT_TAKE_PICTURE = 1;
    static int RESULT_REQUEST_PERMISSION = 2;
    String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //obtendo elemento tbMain
        Toolbar toolbar = findViewById(R.id.tbMain);
        //indicando para a MainActivity que tbMain deve ser considerado como a ActionBar padrao
        setSupportActionBar(toolbar);

        //acessando o diretorio "Pictures"
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Lendo a lista de fotos ja salvas
        File[] files = dir.listFiles();
        //adicionando na lista de fotos
        for(int i = 0; i < files.length; i++) {
            photos.add(files[i].getAbsolutePath());
        }

        //criando o mainAdapter
        mainAdapter = new MainAdapter(MainActivity.this,photos);

        //setando no RecycleView
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);

        //calculando quantas colunas de fotos cabem na tela do celular
        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this, w);
        //configurando o RecucleView para exibir as fotos em GRID, respeitando o numero maximo de colunas
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);

        //chamando os metodos que pedem a prmissao de uso de camera
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);

        checkForPermissions(permissions);
    }

    /*este metodo cria um inflador de menu que cria as opcoes de menu definida no arquivo de menu
    e as adiciona no menu da Activity*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;
    }

    /*Metodo sera chamado sempre que um item da ToolBar for selecionado*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            //caso o icone da camera seja clicado a camera sera disparada
            case R.id.opCamera:
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*recebe como parametro qual foto devera ser aberto por PhotoActivity, que eh chamado
    dentro de onBindViewHolder(MainAdapter) quando o usuario clica em uma foto */
    public void startPhotoActivity(String photoPath) {
        //passando o caminho para a foto para PhotoActivity via intent
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);
    }

//metodo que dispara a camera a app camera
    private void dispatchTakePictureIntent(){
        //criando arquivo vazio detro da pasta Picture
        File f = null;
        //caso o arquivo nao possa ser criado, eh exibido uma mensagem para o usuario e o metodo retorna
        try {
            f = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Não foi possivel criar o arquivo", Toast.LENGTH_SHORT).show();
            return;
        }

        //uma vez criado o arquivo, o local do memo eh salvo no atributo de classe currentPhotoPath
        currentPhotoPath = f.getAbsolutePath();

        if(f != null) {
            //geracao de um endereco URI para o arquivo de foto
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "gouvea.lopes.mariana.galeria.fileprovider", f);
            //criacao do intent para disparar a camera
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //passando URI para a app de camera via Intent
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            //app de camera eh iniciada e a app fica a espera do resultado, no caso a foto
            startActivityForResult(i, RESULT_TAKE_PICTURE);
        }
    }

    //metodo que cria o arquivo que vai guardar a imagem
    private  File createImageFile() throws  IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
    }

    //depois que a app de camera retorna para a nossa aplicacao, este metodo eh chamado
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //caso a foto tenha sido tirada
        if(requestCode == RESULT_TAKE_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                //local da foto eh adicionado na lista de fotos
                photos.add(currentPhotoPath);
                //e o mainAdapter eh avisado de que uma nova foto foi inserida na lista
                mainAdapter.notifyItemInserted(photos.size()-1);
            }
            //caso a foto nao tenha sido tirada
            else {
                //o arquivo criado para conter a foto eh excluido
                File f = new File(currentPhotoPath);
                f.delete();
            }
        }
    }

    //metodo que aceita como entrada uma lista de permissoes
    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();

        //todas as permissoes sao vericadas
        for(String permission : permissions){
            //caso o usuario nao tenha ainda confirmado uma permissao
            if(!hasPermission(permission)) {
                //esta permissao eh posta em lista de permissoes nao confirmadas ainda
                permissionsNotGranted.add(permission);
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsNotGranted.size() > 0) {
                //as permissoes nao concedidas sao requisitadas ao usuario
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
            }
        }
    }

    //metodo que verifica se uma determinada permissao ja foi concedida ou nao
    private boolean hasPermission(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    //metodo que eh chamado apos o usuario conceder ou nao as permissoes requisitadas
    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        if (requestCode == RESULT_REQUEST_PERMISSION) {
            //para cada pemrissao eh verificado se a mesma foi concedida ou nao
            for (String permission : permissions) {
                if (!hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }

        //caso ainda tenha alguma permissao que nao foi concedida
        if (permissionsRejected.size() > 0) {
            //e ela eh necessaria para o funconamento correto da app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //entao eh exibida uma mensaagem ao usuario infrmado que a permissao eh realmente necessaria
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso\n" +
                            "conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //e novamente sao requisitadas as permissoes
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]),RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }
    }
}