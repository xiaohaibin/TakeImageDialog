package com.stx.xhb.library;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;


/**
 * ChooseImageDialog
 * Description:统一选择图片对话框
 */
public class ChooseImageDialog extends BaseDialogFragment implements View.OnClickListener {

    private final static int REQUEST_CAMERA = 0x123;
    private final static int REQUEST_ALBUM = 0x124;
    private File tempFile;

    private Operator operator;

    public static ChooseImageDialog newInstance() {
        Bundle args = new Bundle();
        ChooseImageDialog fragment = new ChooseImageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.dialog_choose_photo_method);
        findViewById(R.id.choose_photo_take).setOnClickListener(this);
        findViewById(R.id.choose_photo_system).setOnClickListener(this);
        findViewById(R.id.choose_photo_cancel).setOnClickListener(this);
    }


    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        return new ViewGroup.LayoutParams(ScreenUtil.getScreenWidth(getActivity()), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getGravity() {
        return Gravity.BOTTOM;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    if (operator != null) {
                        operator.onGetImage(tempFile.getPath());
                        dismiss();
                    }
                    break;
                case REQUEST_ALBUM:
                    if (!getResultFromAlbum(data)) {
                        Toast.makeText(getActivity(), "选取图片失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理相册图片返回结果
     */
    private boolean getResultFromAlbum(Intent data) {
        if (data == null) {
            return false;
        }
        Uri selectedImage = data.getData();
        if (selectedImage == null) {
            return false;
        }
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = getActivity().getContentResolver().query(selectedImage, filePathColumns, null, null, null);
        if (c == null) {
            return false;
        }
        if (!c.moveToFirst()) {
            return false;
        }
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        if (operator != null) {
            operator.onGetImage(picturePath);
            dismiss();
        }
        return true;
    }

    private void init() {
        final String fileName = System.currentTimeMillis() + "";
        if (getActivity().getExternalCacheDir() == null) {
            tempFile = new File(getActivity().getCacheDir().getPath(), fileName);
        } else {
            tempFile = new File(getActivity().getExternalCacheDir().getPath(), fileName);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.choose_photo_take) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri uriForFile = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                }else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                }
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        } else if (i == R.id.choose_photo_system) {
            Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(picture, REQUEST_ALBUM);
        } else if (i == R.id.choose_photo_cancel) {
            dismiss();
        }
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public interface Operator {

        void onGetImage(String path);

    }

}
