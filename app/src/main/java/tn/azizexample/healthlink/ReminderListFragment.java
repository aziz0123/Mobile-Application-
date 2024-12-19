package tn.azizexample.healthlink;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import tn.azizexample.healthlink.database.DatabaseHelper;
import tn.azizexample.healthlink.model.rappel;

public class ReminderListFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button addReminderButton;
    private DatabaseHelper databaseHelper;
    private ReminderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        addReminderButton = view.findViewById(R.id.addReminderButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());
        List<rappel> rappelList = databaseHelper.getTousLesRappels();

        // Initialize adapter with the current fragment as the parentFragment
        adapter = new ReminderAdapter(getContext(), rappelList, this);
        recyclerView.setAdapter(adapter);

        // Listener for add reminder button
        addReminderButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ReminderFormFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}


