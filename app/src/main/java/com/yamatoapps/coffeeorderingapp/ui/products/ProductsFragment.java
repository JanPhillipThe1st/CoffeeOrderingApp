package com.yamatoapps.coffeeorderingapp.ui.products;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yamatoapps.coffeeorderingapp.Coffee;
import com.yamatoapps.coffeeorderingapp.CoffeeAdapter;
import com.yamatoapps.coffeeorderingapp.CoffeeAdapterAdmin;
import com.yamatoapps.coffeeorderingapp.R;
import com.yamatoapps.coffeeorderingapp.databinding.FragmentProductsBinding;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {

private FragmentProductsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ProductsViewModel homeViewModel =
                new ViewModelProvider(this).get(ProductsViewModel.class);

    binding = FragmentProductsBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final GridView gvProducts = binding.gvProducts;
        ArrayList<Coffee> products = new ArrayList<>();
        CoffeeAdapterAdmin adapter = new CoffeeAdapterAdmin(root.getContext(), products);
        //adapter.add();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("coffee").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Coffee coffee = new Coffee(document.getString("name"),document.getString("description"),document.getString("size"),Double.parseDouble(document.get("price").toString()),document.getString("image_url"));
                    coffee.id = document.getId();
                    adapter.add(coffee);
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