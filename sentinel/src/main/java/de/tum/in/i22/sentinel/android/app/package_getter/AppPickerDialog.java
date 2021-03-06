package de.tum.in.i22.sentinel.android.app.package_getter;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.i22.sentinel.android.app.R;

/**
 * Created by laurentmeyer on 31/12/15.
 */

/**
 * This class displays dialog with an infinite list view of all apps available on the device (3rd party apps)
 * Triggers the #PackageGetter.Callback when the user makes his choice
 */
public class AppPickerDialog extends Dialog implements PackageGetter.Callback {

    ListView lv;
    TextView tv;
    Button b;
    ProgressBar pb;

    List<PackageGetter.Package> packages;
    AllApkAdapter adapter;

    OnPackageChosen callback;
    onFileChooseTriggered startActivityCallback;

    /**
     * Triggers the view creation methods
     * @param context: the context needs to be there to start every dialog.
     * @param callback: the callback to be called once the user makes his choice
     * @param onClickListener: the file chooser activity trigger which will be called if the user clicks the "Pick" button
     */
    public AppPickerDialog(Context context, OnPackageChosen callback, onFileChooseTriggered onClickListener) {
        super(context, R.style.CustomPontusStyle);
        this.startActivityCallback = onClickListener;
        this.callback = callback;
        init();
    }


    /**
     * Very big method setting all the view parameters and starting the search of the installed package in an external thread
     */
    private void init(){
        packages = new ArrayList<>();
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = li.inflate(R.layout.app_picker_dialog_layout, null);

        lv = (ListView) v.findViewById(R.id.allAppList);
        tv = (TextView)v.findViewById(R.id.pickFromFile);
        b = (Button) v.findViewById(R.id.pickButton);
        pb = (ProgressBar) v.findViewById(R.id.progressBar);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityCallback.onClick();
            }
        });
        // Nothing but the ProgressBar should be visible until the package are loaded
        b.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);
        lv.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);
        setContentView(v);

        // Look for installed packages
        Thread t = new Thread(){
            @Override
            public void run() {
                PackageGetter.getPackages(AppPickerDialog.this, getContext());
            }
        };
        t.start();
    }

    @Override
    public void onError(Exception e) {
        dismiss();
    }

    @Override
    public void onSuccess(final List<PackageGetter.Package> packages) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AppPickerDialog.this.packages = packages;
                adapter = new AllApkAdapter();
                adapter.notifyDataSetChanged();
                lv.setAdapter(adapter);
                lv.setVisibility(View.VISIBLE);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (callback != null){
                            // The modulo is due to the infinite listView
                            callback.onPackageSet(packages.get(i % packages.size()));
                        }
                    }
                });
                // We have the packages yet, we can display them
                lv.setSelectionFromTop(adapter.MIDDLE, 0);
                tv.setVisibility(View.VISIBLE);
                b.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
        });
    }

    /**
     * This interface is implemented on the other side to detect when the user chooses a
     * package to be instrumented
     */
    public interface OnPackageChosen {
        void onPackageSet(PackageGetter.Package selectedPackage);
    }


    /**
     * This interface is implemented on the other side to detect when the user chooses to pick a
     * File as APK to be instrumented
     */
    public interface onFileChooseTriggered{
        void onClick();
    }

    /**
     * Infinite listView adapter
     */
    private class AllApkAdapter extends BaseAdapter {

        public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
        public final int MIDDLE;

        private AllApkAdapter(){
            MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % packages.size();
        }

        // Should be as big a possible
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        // Use modulo, be clever ;)
        @Override
        public Object getItem(int i) {
            return packages.get(i % packages.size());
        }

        // Not used anyway
        @Override
        public long getItemId(int i) {
            return i;
        }

        // Set a standard view to the row
        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.app_list_row, null);

            TextView rowItemTitle = (TextView) v.findViewById(R.id.rowTextTitle);
            TextView rowItemPath = (TextView) v.findViewById(R.id.rowTextPath);
            ImageView rowItemIcon = (ImageView) v.findViewById(R.id.rowIcon);

            rowItemTitle.setText(((PackageGetter.Package)getItem(i)).getName());
            rowItemPath.setText(((PackageGetter.Package)getItem(i)).getPath());
            rowItemIcon.setImageDrawable(((PackageGetter.Package) getItem(i)).getPackagePicture());

            return v;
        }
    }

}
