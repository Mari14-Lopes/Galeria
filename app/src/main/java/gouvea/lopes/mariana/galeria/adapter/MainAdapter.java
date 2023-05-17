package gouvea.lopes.mariana.galeria.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gouvea.lopes.mariana.galeria.MainActivity;
import gouvea.lopes.mariana.galeria.R;
import gouvea.lopes.mariana.galeria.util.Util;

public class MainAdapter extends RecyclerView.Adapter {
    MainActivity mainActivity;
    List<String> photos;

    public MainAdapter(MainActivity mainActivity, List<String> photos){
        //instancia para a classe MainActivity
        this.mainActivity = mainActivity;
        //lista de Strings, cada string representa um caminho para uma foto
        this.photos = photos;
    }

    /*onCreateViewHolder -> responsável por criar os elementos de interface
    para um item. Esses elementos são guardados em uma classe container do
    tipo ViewHolder.*/
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        /*inflador de layouts que será
        usado para ler o arquivo xml de layout do item e
        então criar os elementos de interface propriamente ditos*/
        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        //esse objeto do tipo View (v) é guardado dentro de um objeto do tipo MyViewHolder
        View v = inflater.inflate(R.layout.list_item,parent,false);
        return new ViewHolder(v);
    }

    //getItemCount -> informa quantos elementos a lista possui;
    @Override
    public  int getItemCount(){
        return photos.size();
    }

    /*onBindViewHolder -> recebe o ViewHolder criado por
    onCreateViewHolder e preenche os elementos de UI com os dados do item;
    holder -> objeto do tipo ViewHolder que guarda os itens de interface criados na
    execução de onCreateViewHolder;
●   position -> indica qual elemento da lista deve ser usado para preencher o item.*/
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position){
        //preenchend o ImageView com a foto correspondente
        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);
        //obtendo as dimensoes que a imagem vai ter na lista
        int w = (int) mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int) mainActivity.getResources().getDimension(R.dimen.itemHeight);
        //carregando a imagem em um bitmap ao mesmo tempo em que a foot é escalada para casar com os tamanhos definidos para o imageView
        Bitmap bitmap = Util.getBitmap(photos.get(position), w, h);
        //setando bitmap no imageView
        imPhoto.setImageBitmap(bitmap);
        //ao clicar em cima da imagem a app navega para PhotoActivity, cuja funcao eh exibir a fotoe tamanho ampliado
        imPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.startPhotoActivity(photos.get(position));
            }
        });
    }

}
