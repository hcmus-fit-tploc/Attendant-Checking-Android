package com.example.hoangdang.diemdanh.studentQuiz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdang.diemdanh.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {

    @BindView(R.id.list_detail)
    ListView listView;

    public ArrayList<String> answers;
    public ArrayList<String> correct_answers;
    public ArrayList<Boolean> corrects;

    private OnFragmentInteractionListener mListener;

    public DetailFragment() {}

    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, view);

        //answers = ((StudentQuizActivity)getActivity()).getAnswers();
        //correct_answers = ((StudentQuizActivity)getActivity()).getCorrect_answers();
        //corrects = ((StudentQuizActivity)getActivity()).getCorrects();

        setButtonListener();

        return view;
    }

    private void setButtonListener(){
        listView.setAdapter(new ListViewAdapter(answers,correct_answers,corrects,getContext()));
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //Function
    private void displayToast(String toast) {
        if(getActivity() != null) {
            Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
        }
    }

    public class ListViewAdapter extends BaseAdapter implements ListAdapter {
        ArrayList<String> answers;
        ArrayList<String> correct_answers;
        ArrayList<Boolean> corrects;

        Context context;

        View view;

        public ListViewAdapter(ArrayList<String> answers,
                           ArrayList<String> correct_answers,
                           ArrayList<Boolean> corrects, Context context){
            this.answers = answers;
            this.correct_answers = correct_answers;
            this.corrects = corrects;
            this.context = context;
        }

        @Override
        public int getCount() {
            return corrects.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            view = convertView;
            if (view == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_question_result, null);
            }

            TextView textView = (TextView)view.findViewById(R.id.your_a);
            textView.setText(answers.get(position).toUpperCase());

            TextView textView_r = (TextView)view.findViewById(R.id.correct_answer);
            textView_r.setText(correct_answers.get(position).toUpperCase());

            TextView csign = (TextView)view.findViewById(R.id.correct_sign);
            TextView icsign = (TextView)view.findViewById(R.id.incorrect_sign);
            if (corrects.get(position)){
                icsign.setVisibility(View.GONE);
                csign.setVisibility(View.VISIBLE);
            }
            else {
                csign.setVisibility(View.GONE);
                icsign.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

    //Network task
}
