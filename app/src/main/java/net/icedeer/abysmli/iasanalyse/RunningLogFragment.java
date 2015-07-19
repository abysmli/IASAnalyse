package net.icedeer.abysmli.iasanalyse;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RunningLogFragment extends Fragment {


    public RunningLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_running_log, container, false);
        TextView textView = (TextView) view.findViewById(R.id.running_log_text);
        StringBuffer sb = LogRecorder.readLog(getActivity());
        textView.setText(Html.fromHtml(sb.toString()));
        return view;
    }

    public void clear_log() {
        TextView textView = (TextView) getView().findViewById(R.id.running_log_text);
        textView.setText("");
    }
}
