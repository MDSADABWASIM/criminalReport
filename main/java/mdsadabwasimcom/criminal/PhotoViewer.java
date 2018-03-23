package mdsadabwasimcom.criminal;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

//the dialog fragment to zoom the image of suspect when clicked.
public class PhotoViewer extends DialogFragment {
    private static final String ARG_PHOTO_FILE = "photoFile";

    private ImageView mPhotoView;
    private File mPhotoFile;

    public static PhotoViewer newInstance(File photoFile) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_FILE, photoFile);

        PhotoViewer fragment = new PhotoViewer();
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mPhotoFile = (File) getArguments().getSerializable(ARG_PHOTO_FILE);

        View view = inflater.inflate(R.layout.view_photo, container, false);

        mPhotoView = view.findViewById(R.id.photo_view_dialog);

        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

        return view;
    }
}



