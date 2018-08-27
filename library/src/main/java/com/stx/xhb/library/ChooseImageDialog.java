package com.stx.xhb.library;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * ChooseImageDialog
 * @author xiao.haibin
 * Description:统一选择图片对话框
 */
public class ChooseImageDialog extends BaseDialogFragment implements View.OnClickListener {

    private final static int REQUEST_CAMERA = 0x123;
    private final static int REQUEST_ALBUM = 0x124;
    private int REQUEST_CODE_PERMISSION = 0x00099;
    //权限申请回调
    private OnPermissionResponseListener onPermissionResponseListener;
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
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomDialog);
        init();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_choose_photo_method;
    }

    @Override
    public void bindview(View v) {
        findViewById(R.id.choose_photo_take).setOnClickListener(this);
        findViewById(R.id.choose_photo_system).setOnClickListener(this);
        findViewById(R.id.choose_photo_cancel).setOnClickListener(this);
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
            requestPermission(new String[]{Manifest.permission.CAMERA}, new OnPermissionResponseListener() {
                @Override
                public void onSuccess(String[] permissions) {
                    strartCamera();
                }

                @Override
                public void onFail() {

                }
            });

        } else if (i == R.id.choose_photo_system) {
            requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnPermissionResponseListener() {
                @Override
                public void onSuccess(String[] permissions) {
                    startPictureActivity();
                }

                @Override
                public void onFail() {

                }
            });
        } else if (i == R.id.choose_photo_cancel) {
            dismiss();
        }
    }

    private void strartCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uriForFile = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void startPictureActivity() {
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, REQUEST_ALBUM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (verifyPermissions(grantResults)) {
                if (onPermissionResponseListener != null) {
                    onPermissionResponseListener.onSuccess(permissions);
                }
            } else {
                if (onPermissionResponseListener != null) {
                    onPermissionResponseListener.onFail();
                }
                showTipsDialog();
            }
        }
    }


    public void requestPermission(String[] permissions, OnPermissionResponseListener onPermissionResponseListener) {
        this.onPermissionResponseListener = onPermissionResponseListener;
        if (checkPermissions(permissions)) {
            if (onPermissionResponseListener != null) {
                onPermissionResponseListener.onSuccess(permissions);
            }
        } else {
            List<String> needPermissions = getDeniedPermissions(permissions);
            requestPermissions(needPermissions.toArray(new String[needPermissions.size()]), REQUEST_CODE_PERMISSION);
        }
    }


    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) !=
                    PackageManager.PERMISSION_GRANTED ||
                    shouldShowRequestPermissionRationale( permission)) {
                needRequestPermissionList.add(permission);
            }
        }
        return needRequestPermissionList;
    }


    public boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private interface OnPermissionResponseListener {
        void onSuccess(String[] permissions);

        void onFail();
    }


    private void showTipsDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.string_waring))
                .setMessage(getString(R.string.string_request_permission_tips))
                .setNegativeButton(getString(R.string.string_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(getString(R.string.string_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                }).show();
    }


    public void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivity(intent);
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public interface Operator {

        void onGetImage(String path);

    }

}
