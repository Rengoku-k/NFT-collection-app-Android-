package edu.uncc.finalexam;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import edu.uncc.finalexam.databinding.FragmentNftsBinding;
import edu.uncc.finalexam.databinding.NftRowItemBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NftsFragment extends Fragment {
    public NftsFragment() {
        // Required empty public constructor
    }

    ListenerRegistration listenerRegistration = null;

    ArrayList<Favourites>favourites=new ArrayList<>();
    ArrayList<NFT> nfts=new ArrayList<>();;
    FragmentNftsBinding binding;
    NFTsAdapter adapter;

    private final OkHttpClient client = new OkHttpClient();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNftsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    void getFavourites(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        listenerRegistration = db.collection("favourites").whereEqualTo("uid", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.w("demo", "Listen failed.", error);
                            return;
                        }

                        favourites.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Favourites fav = doc.toObject(Favourites.class);
                            favourites.add(fav);
                        }

                        adapter.notifyDataSetChanged();

                    }
                });
    }
void getNFTS(){

    Request request = new Request.Builder()
            .url("https://www.theappsdr.com/api/nfts-assets")
            .build();
    client.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if(response.isSuccessful()){
                String body=response.body().string();

                try {
                    JSONObject jsonObject=new JSONObject(body);
                    JSONArray jsonArray=jsonObject.getJSONArray("assets");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject nftObject = jsonArray.getJSONObject(i);
                        NFT nft=new NFT(nftObject);
                    nfts.add(nft);

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
            else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Unable to fetch NFTS", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    });
}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        adapter = new NFTsAdapter();
        binding.recyclerViewNfts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewNfts.setAdapter(adapter);
        getNFTS();
        binding.buttonNftSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(nfts, new Comparator<NFT>() {
                    @Override
                    public int compare(NFT nft, NFT t1) {
                        return nft.nft_name.compareToIgnoreCase(t1.nft_name);
                    }


                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            };

        });
        binding.buttonCollectionSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(nfts, new Comparator<NFT>() {
                    @Override
                    public int compare(NFT nft, NFT t1) {
                        return nft.collection_name.compareToIgnoreCase(t1.collection_name);
                    }


                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_logout){
            mListener.logout();
        }
        return super.onOptionsItemSelected(item);
    }
    class NFTsAdapter extends RecyclerView.Adapter<NFTsAdapter.NFTsViewHolder> {

        @NonNull
        @Override
        public NFTsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NFTsViewHolder(NftRowItemBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NFTsViewHolder holder, int position) {
            NFT nft = nfts.get(position);
            holder.setupUI(nft);
        }

        @Override
        public int getItemCount() {
            return nfts.size();
        }

        class NFTsViewHolder extends RecyclerView.ViewHolder {
            NFT mNFT;
            NftRowItemBinding mBinding;
            public NFTsViewHolder(NftRowItemBinding mBinding) {
                super(mBinding.getRoot());
                this.mBinding = mBinding;
                mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }

            void setupUI(NFT nft){
                this.mNFT = nft;
                mBinding.textViewNftName.setText(mNFT.nft_name);
                mBinding.textViewCollectionName.setText(mNFT.collection_name);
                Picasso.get().load(mNFT.getImage_thumbnail_url()).into(mBinding.imageViewNftIcon);
                Picasso.get().load(mNFT.getCollection_image()).into(mBinding.imageViewCollectionBanner);
mBinding.imageViewAddRemove.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("favourites").document();

        HashMap<String, Object> data = new HashMap<>();
        data.put("nft_id", mNFT.getId());
        data.put("uid",FirebaseAuth.getInstance().getUid());
        data.put("docId",docRef.getId());

        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Added successfully!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
});

            }
        }
    }
    NftsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (NftsListener) context;
    }

    interface NftsListener {
        void logout();
    }
}