package com.example.myfinal.Patient;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinal.Model.Medicine;
import com.example.myfinal.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class PatientSeeMedicineFragment extends Fragment {

    private RecyclerView rcv;
    private ImageView back;
    private TextView tv_dr;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String patientID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_see_medicine, container, false);

        back = (ImageView) view.findViewById(R.id.back);
        tv_dr = (TextView) view.findViewById(R.id.tv_dr);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientHomeFragment patientHomeFragment = new PatientHomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_medicine, patientHomeFragment).commit();
            }
        });

        patientID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

        rcv = (RecyclerView) view.findViewById(R.id.rcv);

        Query query = firebaseFirestore
                .collection("Recipe")
                .whereEqualTo("id_patient", patientID);
        FirestoreRecyclerOptions<Medicine> options =
                new FirestoreRecyclerOptions.Builder<Medicine>().setQuery(query, Medicine.class).build();
        adapter = new FirestoreRecyclerAdapter<Medicine,
                MedicineViewHolder>(options) {

            @NonNull
            @Override
            public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent,
                        false);
                return new MedicineViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull MedicineViewHolder holder, int position,
                                            @NonNull Medicine model) {
                holder.tv_name.setText(model.getName());
                holder.tv_dosage.setText(model.getDosage());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                // Lấy giá trị nameDoctor từ phần tử đầu tiên trong danh sách
                String nameDoctor = "";
                if (getItemCount() > 0) {
                    nameDoctor = getItem(0).getNameDoctor();
                }
                tv_dr.setText("Written by " + nameDoctor);
            }
        };

        adapter.startListening();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                adapter.notifyDataSetChanged();
                if (adapter.getItemCount() > 0) {
                    rcv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    rcv.setAdapter(adapter);
                } else {
                    tv_dr.setText("No recipe");
                }
            }
        });

        return view;
    }

    private class MedicineViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_dosage;

        public MedicineViewHolder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_dosage = itemView.findViewById(R.id.tv_dosage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
