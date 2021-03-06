package de.tum.in.i22.sentinel.android.app.file_explorer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tum.in.i22.sentinel.android.app.Constants;
import de.tum.in.i22.sentinel.android.app.R;

/**
 * Created by Moderbord on 2016-01-06.
 */
public class DirectoryChooser extends ListActivity {

    private File workingDir;
    private FileArrayAdapter adapter;
    private final String SET_DIRECTORY = "Choose this directory";
    private String storageDirectoryPath;

    /**
     * Assign the path to start from and fire the view creation method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageDirectoryPath = Environment.getExternalStorageDirectory().getPath();
        workingDir = new File(storageDirectoryPath);
        fill(workingDir);
    }

    /**
     * This methods fills the adapter with a human readable content representing the structure of
     * the current directory
     *
     * @param workingDir:
     */
    private void fill(File workingDir) {
        File[] folder = workingDir.listFiles();
        this.setTitle("Current directory: " + workingDir.getName());
        List<MenuObj> dirs = new ArrayList<MenuObj>();

        try {
            for (File f : folder) {
                // Sets the last modified date in the directory
                String formattedDate = "";

                // If there is a sub directory, it displays the count of contained files
                // Condition: If it is not a hidden directory
                if (f.isDirectory() && !f.isHidden()) {
                    File[] subDir = f.listFiles();
                    int amount = 0;
                    if (subDir != null) {
                        amount = subDir.length;
                    }
                    String numItem = String.valueOf(amount);
                    // Singular/Plural
                    if (amount <= 1) {
                        numItem = numItem + " item";
                    } else {
                        numItem = numItem + " items";
                    }

                    // Creates a directory MenuObj
                    dirs.add(new MenuObj(f.getName(), numItem, formattedDate, f.getAbsolutePath(), Constants.DIRECTORY_ICON));

                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Sort the list of folders
        Collections.sort(dirs);

        // If working directory isn't in root, append a MenuObj for user to select this directory
        // and another MenuObj to be able to navigate to parent directory
        if (!workingDir.getPath().equalsIgnoreCase(storageDirectoryPath)) {
            // 0 & 1 because we force these items to be at the top of the screen
            dirs.add(0, new MenuObj(SET_DIRECTORY, "", "", workingDir.getParent(), Constants.DIRECTORY_PICK));
            dirs.add(1, new MenuObj("..", Constants.PARENT_DIRECTORY, "", workingDir.getParent(), Constants.DIRECTORY_UP));
        }
        adapter = new FileArrayAdapter(DirectoryChooser.this, R.layout.instrument_fragment, R.layout.file_explorer, dirs);
        this.setListAdapter(adapter);
    }

    /**
     * Inherited method to handle the click on a item:
     * We have two possibilities:
     *  - It is a folder and has been clicked, so we get in this folder
     *  - It is the "Choose this directory" button and then we need to return
     * @param l: the list view
     * @param v: the clicked view
     * @param position: the position of the view in the ListView
     * @param id: the id of the View
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MenuObj obj = adapter.getItem(position);
        if (obj.getState() == MenuObj.STATE.FOLDER) {
            // It is a folder, we do nothing
            workingDir = new File(obj.getPath());
            fill(workingDir);
        } else {
            // It's not a folder, if it's a File, we do nothing, however, we need to check that it is not the "Choose this directory" button
            boolean isChooseDirectoryButton = obj.getName().equals(SET_DIRECTORY);
            if (isChooseDirectoryButton) {
                directoryChosen();
            }
        }
    }

    /**
     * Once the directory has been chosen by the user, we return an Ok Status
     * {@see android.app.Activity#RESULT_OK}
     */
    private void directoryChosen() {
        Intent intent = new Intent();
        intent.putExtra(Constants.DIRECTORY_PATH, workingDir.toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
