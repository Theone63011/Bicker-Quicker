package purdue.edu.bicker_quicker;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class CodeDialog extends AppCompatDialogFragment {

    TextView code;

    CodeDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CodeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement CodeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_code, null);

        builder.setView(view)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // backToMain();
                        listener.leave();
                    }
                });

        String newCode = getArguments().getString("code"); // Get code from bundle
        code = view.findViewById(R.id.textCode);
        code.setText(newCode);

        return builder.create();
    }

    public interface CodeDialogListener {
        void leave();
    }
}
