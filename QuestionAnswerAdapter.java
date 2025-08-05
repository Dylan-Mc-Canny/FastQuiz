package com.example.dissertation_tester;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAnswerAdapter extends RecyclerView.Adapter<QuestionAnswerAdapter.ViewHolder> {

    private List<QuestionAnswerItem> questionAnswerList;
    private QuestionAnswerActivity activity;

    public QuestionAnswerAdapter(List<QuestionAnswerItem> questionAnswerList, QuestionAnswerActivity activity) {
        this.questionAnswerList = questionAnswerList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionAnswerItem item = questionAnswerList.get(position);

        holder.tvQuestion.setText(item.getQuestion());
        holder.tvAnswer.setText(item.getAnswer());

        holder.btnEdit.setOnClickListener(v ->
                activity.showEditQuestionDialog(item.getQuestion(), item.getAnswer()));

        holder.btnDelete.setOnClickListener(v ->
                activity.showDeleteConfirmationDialog(item.getQuestion()));
    }

    @Override
    public int getItemCount() {
        return questionAnswerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}