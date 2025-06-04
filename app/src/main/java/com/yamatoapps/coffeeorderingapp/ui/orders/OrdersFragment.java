package com.yamatoapps.coffeeorderingapp.ui.orders;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yamatoapps.coffeeorderingapp.Coffee;
import com.yamatoapps.coffeeorderingapp.CoffeeAdapterAdmin;
import com.yamatoapps.coffeeorderingapp.Order;
import com.yamatoapps.coffeeorderingapp.OrderAdapter;
import com.yamatoapps.coffeeorderingapp.databinding.FragmentOrdersBinding;

import java.sql.Time;
import java.util.ArrayList;

public class OrdersFragment extends Fragment {

private FragmentOrdersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        OrdersViewModel notificationsViewModel =
                new ViewModelProvider(this).get(OrdersViewModel.class);

    binding = FragmentOrdersBinding.inflate(inflater, container, false);
    View root = binding.getRoot();
        final GridView gvProducts = binding.gvProducts;
        ArrayList<Order> products = new ArrayList<>();
        OrderAdapter adapter = new OrderAdapter(root.getContext(), products);
        //adapter.add();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("coffee_orders").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    adapter.add(new Order(document.getString("name"),document.getDate("date_ordered", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),Double.parseDouble(document.get("price").toString()),document.getString("image_url")));
                }
                gvProducts.setAdapter(adapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}