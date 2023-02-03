package com.google.ar.sceneform.samples.gltf;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.samples.gltf.adapter.CoverAdapter;
import com.google.ar.sceneform.samples.gltf.adapter.SearchAdapter;
import com.google.ar.sceneform.samples.gltf.model.Cover;
import com.google.ar.sceneform.samples.gltf.model.Furniture;
import com.google.ar.sceneform.samples.gltf.model.OnClickCover;
import com.google.ar.sceneform.samples.gltf.model.OnClickFurniture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileFragment extends Fragment {
    ImageView imageProfile,cover;
    TextView name, phone;
    ImageView exit;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    CoverAdapter coverAdapter;
    List<Cover> coverList;
    Dialog dialog;
    Map<String,Cover> coverMap;
    View slide1,slide2;
    LinearLayout cl,infor,lycl,lyinfor;
    TextView txtcl,txtinfor;
    EditText edtname;
    Button btnrename;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        name = getView().findViewById(R.id.name);
        phone = getView().findViewById(R.id.phone);
        cover = getView().findViewById(R.id.cover);
        imageProfile = getView().findViewById(R.id.profile_image);
        recyclerView = getView().findViewById(R.id.recycler_view_collection);
        coverList = new ArrayList<Cover>();
        coverMap = new HashMap<String,Cover>();
        coverAdapter = new CoverAdapter(coverList, new OnClickCover() {
            @Override
            public void onClickCover(Cover cover, View view) {
                showImage(cover);
            }

            @Override
            public void onLongClickCover(Cover cover, View view) {
                ShowPopUpMenu(cover,view);
            }
        });
        recyclerView.setAdapter(coverAdapter);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Glide.with(this).load(user.getPhotoUrl().toString()).into(imageProfile);
            name.setText(user.getDisplayName());
            phone.setText(user.getPhoneNumber());
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Collection").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coverMap.clear();
                coverList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()
                     ) {
                    Cover coverImage = dataSnapshot.getValue(Cover.class);
                    coverMap.put(dataSnapshot.getKey(),coverImage);

                    if (coverImage != null && coverImage.isIscover() == true) {
                        Glide.with(ProfileFragment.this).load(coverImage.getLink()).into(cover);
                    }
                    coverList.add(coverImage);
                    coverAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageProfile.setOnClickListener(v->{

        });

        cl = getView().findViewById(R.id.cl);
        infor = getView().findViewById(R.id.infor);
        lycl = getView().findViewById(R.id.lycl);
        lyinfor = getView().findViewById(R.id.lyinfor);
        slide1 = getView().findViewById(R.id.slide1);
        slide2 = getView().findViewById(R.id.slide2);
        txtcl = getView().findViewById(R.id.txtcl);
        txtinfor = getView().findViewById(R.id.txtinfor);
        edtname = getView().findViewById(R.id.edtname);
        btnrename = getView().findViewById(R.id.btnrename);
        exit = getView().findViewById(R.id.exit);
        exit.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), login.class);
            startActivity(intent);
        });

        cl.setOnClickListener(v->{
            lycl.setVisibility(View.VISIBLE);
            lyinfor.setVisibility(View.GONE);
            slide1.setVisibility(View.VISIBLE);
            slide2.setVisibility(View.GONE);
            txtcl.setTextColor(Color.parseColor("#000000"));
            txtinfor.setTextColor(Color.parseColor("#b3b3b3"));
        });
      infor.setOnClickListener(v->{
          lycl.setVisibility(View.GONE);
          lyinfor.setVisibility(View.VISIBLE);
          slide1.setVisibility(View.GONE);
          slide2.setVisibility(View.VISIBLE);
          txtcl.setTextColor(Color.parseColor("#b3b3b3"));
          txtinfor.setTextColor(Color.parseColor("#000000"));

        });
        edtname.setText(user.getDisplayName());
        btnrename.setOnClickListener(v->{
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(edtname.getText().toString())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Rename Success", Toast.LENGTH_SHORT).show();
                                name.setText(user.getDisplayName());
                            }
                        }
                    });
        });


        super.onStart();
    }

    private void ShowPopUpMenu(Cover cover,View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(),view);
        popupMenu.getMenuInflater().inflate(R.menu.collectionmenu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menucover:
                        {
                            for (Map.Entry<String,Cover> pair: coverMap.entrySet()
                                 ) {
                                if(pair.getValue().isIscover() == true){
                                    databaseReference.child(pair.getKey()).child("iscover").setValue(false);
                                }
                                if (pair.getValue().getLink() == cover.getLink()){
                                    databaseReference.child(pair.getKey()).child("iscover").setValue(true);
                                }
                                Toast.makeText(getContext(), "Update Cover Success", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    case R.id.menudel:
                        {
                            for (Map.Entry<String,Cover> pair: coverMap.entrySet()
                            ) {
                                if (pair.getValue().getLink() == cover.getLink()){
                                    databaseReference.child(pair.getKey()).removeValue();
                                    Toast.makeText(getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                                }

                            }
                            break;
                        }
                    case R.id.menuavatar:
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(cover.getLink()))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Update avatar success", Toast.LENGTH_SHORT).show();
                                                Glide.with(ProfileFragment.this).load(user.getPhotoUrl().toString()).into(imageProfile);
                                            }
                                        }
                                    });
                            break;
                        }
                }
                return false;
            }
        });
        popupMenu.show();

    }

    public void showImage(Cover cover) {
        CreateDialog(R.layout.dialog_view);
        dialog.show();
        ImageView imageView = dialog.findViewById(R.id.imageView);
        Glide.with(this).load(cover.getLink()).into(imageView);
    }

    public void CreateDialog(int layout){
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);

        final Window window = dialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(0.5f);
        window.setAttributes(wlp);

        ((ViewGroup)dialog.getWindow().getDecorView())
                .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                        getContext(),android.R.anim.slide_in_left));

    }

}
