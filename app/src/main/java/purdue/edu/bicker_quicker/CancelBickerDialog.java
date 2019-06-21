package purdue.edu.bicker_quicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

public class CancelBickerDialog extends AppCompatDialogFragment {

    CancelBickerDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CancelBickerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" Must handle listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cancel Bicker").setMessage("Are you sure you want to cancel this bicker?")
                .setPositiveButton("No, Take Me Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.cancelBicker();
                    }
                });

        return builder.create();
    }

    public interface CancelBickerDialogListener {
        void cancelBicker();
    }
}
