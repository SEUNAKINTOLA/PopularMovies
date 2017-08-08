package oluwaseun.akintola.movies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import oluwaseun.akintola.movies.domain.MovieBean;

/**
 * Created by AKINTOLA OLUWASEUN on 8/7/2017.
 */

public class PopMoviesAdapter extends RecyclerView.Adapter<PopMoviesAdapter.MovieVH>{

    final private ListItemClickListener mListClickListener;
    private List<MovieBean.AllResults> list;
    private Context context;
    private final static String BASE_URL = "http://image.tmdb.org/t/p/w185//";

    public static boolean loadFromSDCard = false;

    public interface ListItemClickListener{
        void onItemClick(int position);
    }
    public PopMoviesAdapter(List<MovieBean.AllResults> list, ListItemClickListener listener){
        this.list = list;
        mListClickListener = listener;
    }
    @Override
    public MovieVH onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.list_item;
        boolean attachImmediatelyToParent = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, attachImmediatelyToParent);
        MovieVH viewHolder = new MovieVH(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieVH holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(list != null) {
            return list.size();
        }
        return 0;
    }

    public class MovieVH extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        ImageView poster;

        public MovieVH(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.rv_image);
            itemView.setOnClickListener(this);
        }

        void bind(int index){
            MovieBean.AllResults movie = list.get(index);
            if (loadFromSDCard){
                Bitmap bitmap = BitmapFactory.decodeFile(movie.getPoster_path());
                poster.setImageBitmap(bitmap);
            }
            else {
                Picasso.with(context).load(BASE_URL+movie.getPoster_path())
                        .resize(185, 195).into(poster);
            }
        }

        @Override
        public void onClick(View v) {
            int itemClickedIndex = getAdapterPosition();
            mListClickListener.onItemClick(itemClickedIndex);
        }
    }

    public void swapList(List list){
        if(list != null){
            this.list = list;
            this.notifyDataSetChanged();
        }
    }
}
