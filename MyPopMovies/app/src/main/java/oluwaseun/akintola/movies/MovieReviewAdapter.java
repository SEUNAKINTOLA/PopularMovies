package oluwaseun.akintola.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import oluwaseun.akintola.movies.domain.ReviewBean;

/**
 * Created by AKINTOLA OLUWASEUN on 8/7/2017.
 */

public class MovieReviewAdapter  extends RecyclerView.Adapter<MovieReviewAdapter.ReviewHolder>{
    private Context context;
    private List<ReviewBean.AllResults> list;
    private ReviewItemListener reviewItemListener;

    public interface ReviewItemListener{
        void onClick(String id, String comment);
    }
    public MovieReviewAdapter(Context cx, List<ReviewBean.AllResults> reviews, ReviewItemListener listener){
        context = cx;
        list = reviews;
        reviewItemListener = listener;
    }
    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_item, parent, false);
        ReviewHolder holder = new ReviewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        String user = list.get(position).getAuthor();
        String review = list.get(position).getContent();

        holder.comment_by.setText(user);
        holder.comment.setText(review);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshList(List<ReviewBean.AllResults> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    class ReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView comment_by;
        TextView comment;

        public ReviewHolder(View itemView) {
            super(itemView);
            comment_by = (TextView)itemView.findViewById(R.id.author);
            comment = (TextView)itemView.findViewById(R.id.comment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            String id = comment_by.getText().toString();
            String review = comment.getText().toString();
            reviewItemListener.onClick(id, review);
        }
    }
}
