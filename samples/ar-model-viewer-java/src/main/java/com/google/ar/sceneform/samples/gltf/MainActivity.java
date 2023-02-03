package com.google.ar.sceneform.samples.gltf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.NodeParent;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.samples.gltf.adapter.SearchAdapter;
import com.google.ar.sceneform.samples.gltf.adapter.UseAdapter;
import com.google.ar.sceneform.samples.gltf.model.Cover;
import com.google.ar.sceneform.samples.gltf.model.Furniture;
import com.google.ar.sceneform.samples.gltf.model.OnClickFurniture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jcodec.api.android.AndroidSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        FragmentOnAttachListener,
        BaseArFragment.OnTapArPlaneListener,
        BaseArFragment.OnSessionConfigurationListener,
        ArFragment.OnViewCreatedListener,
        OnClickFurniture {

    private ArFragment arFragment;
    private Renderable model;
    private ViewRenderable viewRenderable;
    CardView cardView;
    String name;
    private boolean isRecording = false;
    private HandlerThread videoProcessingThread;
    private Handler videoProcessingHandler;
    private int messages = 60;
    List<Furniture> furnitureList;
    List<Furniture> furnitureListUse;
    DatabaseReference databaseReference;
    SearchAdapter searchAdapter;
    UseAdapter furnitureUseAdapter;
    BottomSheetDialog bottomSheetDialog;
    RecyclerView recyclerViewUse;
    ImageView capture,back;
    Bitmap bitmap;
    FirebaseStorage storage;
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addFragmentOnAttachListener(this);


        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.arFragment, ArFragment.class, null)
                        .commit();
            }
        }

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        cardView = findViewById(R.id.cardView);
        furnitureList = new ArrayList<Furniture>();
        furnitureListUse = new ArrayList<Furniture>();
        recyclerViewUse = findViewById(R.id.recyclerViewUse);
        capture = findViewById(R.id.capture);
        back = findViewById(R.id.back2);
        back.setOnClickListener(v ->{
            finishActivity(0);
        });

        searchAdapter = new SearchAdapter(furnitureList, new OnClickFurniture() {
            @Override
            public void onFurnitureClicked(Furniture furniture) {
                for (Furniture furniture1: furnitureListUse
                     ) {
                    if (furniture1.getName().equals(furniture.getName())){
                        Toast.makeText(MainActivity.this, "Furniture in use", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                    loadModels(furniture.getModel());
                    bottomSheetDialog.dismiss();
                    furnitureListUse.add(furniture);
                    furnitureUseAdapter.notifyDataSetChanged();


            }
        });
        furnitureUseAdapter = new UseAdapter(furnitureListUse, new OnClickFurniture() {
            @Override
            public void onFurnitureClicked(Furniture furniture) {
                loadModels(furniture.getModel());
            }
        });
        recyclerViewUse.setAdapter(furnitureUseAdapter);
        LoadData("Plant");
        LoadData("Chair");
        LoadData("Lamp");
        LoadData("Table");
        LoadData("Other");


        cardView.setOnClickListener(v -> {

            OpenBottomDialog(R.layout.bottom_dialog_layout, R.id.container);

        });

        capture.setOnClickListener(v->{
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            bitmap = Bitmap.createBitmap(arFragment.getArSceneView().getWidth(),
                    arFragment.getArSceneView().getHeight(), conf);
            PixelCopy.request(arFragment.getArSceneView(), bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
                @Override
                public void onPixelCopyFinished(int i) {
                    OpenBottomDialog(R.layout.activity_share, R.id.sharecontainer);
                }
            }, new Handler());


        });


        Intent intent = getIntent();
        Furniture furniture = (Furniture) intent.getSerializableExtra("Furniture");
        name = furniture.getName();
        loadModels(furniture.getModel());
        furnitureListUse.add(furniture);
        furnitureUseAdapter.notifyDataSetChanged();

    }



    private void LoadData(String category) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Furniture").child(category);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Furniture furniture = data.getValue(Furniture.class);
                    furnitureList.add(furniture);
                }
                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }


    void OpenBottomDialog(int layout, int container) {
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(layout, (ConstraintLayout) findViewById(container));
        bottomSheetDialog.setContentView(bottomSheetView);
        if(layout == R.layout.bottom_dialog_layout){
            RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recycler_view);
            recyclerView.setAdapter(searchAdapter);
            EditText search = bottomSheetView.findViewById(R.id.search);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    filter(editable.toString());
                }
            });
        }else{
            ImageView imageView = bottomSheetView.findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
            Button button = bottomSheetView.findViewById(R.id.buttonSave1);
            Button button1 = bottomSheetView.findViewById(R.id.buttonSave2);
            Button button2 = bottomSheetView.findViewById(R.id.buttonShare);
            button.setOnClickListener(v->{
                getImageFromBitmapAndSave();
            });
            button1.setOnClickListener(v->{
                saveToCollection();
            });
            button2.setOnClickListener(v->{
                shareWithFB(bitmap);
            });
        }

        bottomSheetDialog.show();
    }

    private void saveToCollection() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storageRef = storageRef.child("collection/"+System.currentTimeMillis()+".jpg");

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("Collection");
                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(System.currentTimeMillis()+"").setValue(new Cover(uri.toString(),false));
                        Toast.makeText(MainActivity.this, "Saved to collection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    void getImageFromBitmapAndSave(){
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(MainActivity.this, "Screenshot saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void shareWithFB(Bitmap bitmap){
        SharePhoto sharePhoto1 = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();


        ShareContent shareContent = new ShareMediaContent.Builder()
                .addMedium(sharePhoto1)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);
    }

    void filter(String text) {
        List<Furniture> filteredList = new ArrayList<>();
        for (Furniture item : furnitureList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        searchAdapter.filterList(filteredList);
    }



    @Override
    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if (fragment.getId() == R.id.arFragment) {
            arFragment = (ArFragment) fragment;
            arFragment.setOnSessionConfigurationListener(this);
            arFragment.setOnViewCreatedListener(this);
            arFragment.setOnTapArPlaneListener(this);
        }
    }

    @Override
    public void onSessionConfiguration(Session session, Config config) {
        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        }
    }

    @Override
    public void onViewCreated(ArSceneView arSceneView) {
        arFragment.setOnViewCreatedListener(null);
        // Fine adjust the maximum frame rate
        arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL);
    }

    public void loadModels(String uri) {
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
        ModelRenderable.builder()
                .setSource(this, Uri.parse(uri))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(false)
                .build()
                .thenAccept(model -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.model = model;
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });


        ViewRenderable.builder()
                .setView(this, R.layout.view_model_title)
                .build()
                .thenAccept(viewRenderable -> {
                    MainActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.viewRenderable = viewRenderable;
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (model == null || viewRenderable == null) {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the Anchor.
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create the transformable model and add it to the anchor.
        TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
        model.setParent(anchorNode);
        model.setRenderable(this.model)
                .animate(true).start();
        model.select();


        Node titleNode = new Node();
        titleNode.setParent(model);
        titleNode.setEnabled(false);
        titleNode.setLocalPosition(new Vector3(0.0f, 1.0f, 0.0f));
        titleNode.setRenderable(viewRenderable);
        titleNode.setEnabled(true);

        titleNode.setOnTouchListener(new Node.OnTouchListener() {
            @Override
            public boolean onTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                model.setRenderable(null);
                model.removeChild(titleNode);
                return false;
            }
        });

    }

    @Override
    public void onFurnitureClicked(Furniture furniture) {

    }
}



