package com.textmate.dovaj.textmate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.textmate.dovaj.textmate.fragments.ContactsFragment;
import com.textmate.dovaj.textmate.fragments.HomeFragment;
import com.textmate.dovaj.textmate.fragments.dummy.ContactContent;
import com.textmate.dovaj.textmate.helpers.BottomNavigationHelper;
import com.textmate.dovaj.textmate.helpers.RetrofitHelper;
import com.textmate.dovaj.textmate.interfaces.BaseView;
import com.textmate.dovaj.textmate.interfaces.CallbackWrapper;
import com.textmate.dovaj.textmate.interfaces.TextMateAPIInterface;
import com.textmate.dovaj.textmate.models.ContactModel;
import com.textmate.dovaj.textmate.models.RatingResponse;
import com.textmate.dovaj.textmate.models.RatingResult;


import java.net.SocketTimeoutException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.SerializedObserver;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission_group.CONTACTS;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, ContactsFragment.OnListFragmentInteractionListener, EasyPermissions.PermissionCallbacks{
    public  static final int RequestPermissionCode  = 1 ;
    private static final String TAG = "MainActivity";
    private TextView mTextMessage;

    private static final String[] MESSAGES_AND_CONTACTS =
            {Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS};
    private static final int RC_MESSAGE_PERM = 123;
    private static final int RC_CONTACTS_MESSAGES_PERM = 124;
    private static final String TAG_FRAGMENT_ONE = "fragment_one";
    private static final String TAG_FRAGMENT_TWO = "fragment_two";

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void replaceFragment(@NonNull Fragment fragment, @NonNull String tag) {
        if (!fragment.equals(currentFragment)) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_frame, fragment, tag)
                    .addToBackStack(null)
                    .commit();
            currentFragment = fragment;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ONE);
                    if (fragment == null) {
                        fragment = ContactsFragment.newInstance(1);
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_ONE);
                    MainActivity.this.setTitle(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
                    if (fragment == null) {
                        fragment = HomeFragment.newInstance("test", "test2");
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_TWO);
                    MainActivity.this.setTitle(R.string.title_home);
                    return true;
                case R.id.navigation_notifications:
                    fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
                    if (fragment == null) {
                        fragment = HomeFragment.newInstance("test", "test2");
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_TWO);
                    MainActivity.this.setTitle(R.string.title_home);
                    return true;
                case R.id.navigation_account:
                    fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
                    if (fragment == null) {
                        fragment = HomeFragment.newInstance("test", "test2");
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_TWO);
                    MainActivity.this.setTitle(R.string.title_home);
                    return true;
                default:
                    fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
                    if (fragment == null) {
                        fragment = HomeFragment.newInstance("test", "test2");
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_TWO);
                    MainActivity.this.setTitle(R.string.title_home);
                    return true;
            }
        }
    };


    public void onFragmentInteraction(Uri uri){
        //We can keep this empty
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(myToolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationHelper.disableShiftMode(navigation);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }


    private boolean hasContactsPermissions() {
        return EasyPermissions.hasPermissions(this, MESSAGES_AND_CONTACTS[0]);
    }

    private boolean hasSMSAndContactsPermissions() {
        return EasyPermissions.hasPermissions(this, MESSAGES_AND_CONTACTS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            String yes = getString(R.string.yes);
            String no = getString(R.string.no);

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                    this,
                    getString(R.string.returned_from_app_settings_to_activity,
                            hasSMSAndContactsPermissions() ? yes : no),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onListFragmentInteraction(ContactModel item) {
      Log.d(TAG, item.toString());
      Intent intent = new Intent(getBaseContext(), ContactActivity.class);
      Bundle cBundle = new Bundle();
      cBundle.putSerializable("Contact", item);
      intent.putExtras(cBundle);

      startActivity(intent);
    }
}
