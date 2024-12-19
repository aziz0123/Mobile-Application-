package tn.azizexample.healthlink;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.azizexample.healthlink.R;
import tn.azizexample.healthlink.ReminderUpdateFragment;
import tn.azizexample.healthlink.database.DatabaseHelper;
import tn.azizexample.healthlink.model.rappel;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<rappel> rappelList;
    private Context context;
    private DatabaseHelper databaseHelper;
    private Fragment parentFragment;

    public ReminderAdapter(Context context, List<rappel> rappelList, Fragment parentFragment) {
        this.context = context;
        this.rappelList = rappelList;
        this.databaseHelper = new DatabaseHelper(context);
        this.parentFragment = parentFragment;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rappel, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        rappel rappelItem = rappelList.get(position);
        holder.nomMedicamentTextView.setText(rappelItem.getNomMedicament());
        holder.heureRappelTextView.setText(rappelItem.getHeureRappel());

        // Set up Delete button
        holder.deleteButton.setOnClickListener(v -> {
            databaseHelper.supprimerRappel(rappelItem.getId());
            rappelList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Rappel supprimé", Toast.LENGTH_SHORT).show();
        });

        // Set up Edit button
        holder.editButton.setOnClickListener(v -> {
            ReminderUpdateFragment reminderUpdateFragment = new ReminderUpdateFragment();
            Bundle args = new Bundle();
            args.putInt("reminderId", rappelItem.getId());
            args.putString("reminderTitle", rappelItem.getNomMedicament());
            args.putString("reminderTime", rappelItem.getHeureRappel());
            reminderUpdateFragment.setArguments(args);

            parentFragment.getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, reminderUpdateFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return rappelList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView nomMedicamentTextView, heureRappelTextView;
        Button deleteButton, editButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            nomMedicamentTextView = itemView.findViewById(R.id.nomMedicamentTextView);
            heureRappelTextView = itemView.findViewById(R.id.heureRappelTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}
