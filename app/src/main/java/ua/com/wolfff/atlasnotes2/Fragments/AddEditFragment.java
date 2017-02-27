package ua.com.wolfff.atlasnotes2.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import ua.com.wolfff.atlasnotes2.DatabaseConnector;
import ua.com.wolfff.atlasnotes2.MainActivity;
import ua.com.wolfff.atlasnotes2.R;

public class AddEditFragment extends Fragment {
    protected AddEditFragmentListener listener;
    protected long rowID;
    protected Bundle noteInfoBundle;

    protected EditText editText_ID;
    protected EditText editText_Name;
    protected EditText editText_Describe;
    protected EditText editText_DateAdd;


    public AddEditFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;

    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_add_edit,container,false);
        //editText_ID = (EditText) view.findViewById(R.id.editText_ID);
        //editText_Name = (EditText) view.findViewById(R.id.editText_Name);
        //editText_Describe = (EditText) view.findViewById(R.id.editText_Describe);
        //editText_DateAdd = (EditText) view.findViewById(R.id.editText_DateAdd);

        //noteInfoBundle = getArguments(); //null if new note

//        if (noteInfoBundle != null){
//            rowID = noteInfoBundle.getLong(MainActivity.ROW_ID);
//            editText_ID.setText(""+rowID);
//            editText_Name.setText(noteInfoBundle.getString("_name"));
//            editText_Describe.setText(noteInfoBundle.getString("_describe"));
 //           //editText_DateAdd.setText(noteInfoBundle.getString("_year"));
 //       }
 //       Button saveNoteButton = (Button) view.findViewById(R.id.saveNoteButton);
 //       saveNoteButton.setOnClickListener(saveNoteButtonClicked);
        return view;
    }

    private View.OnClickListener saveNoteButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println("=============================================================================================================== ON CLICK");
            if (editText_Name.getText().toString().trim().length()!=0){
                AsyncTask<Object,Object,Object> saveNoteTask = new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params){
                        saveNote();
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object result){
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(),0);
                        listener.onAddEditCompleted(rowID);
                    }
                };
                saveNoteTask.execute((Object[]) null);
            } else {
                DialogFragment errorSaving = new DialogFragment(){
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("ERROR");
                        builder.setPositiveButton("OK",null);
                        return builder.create();
                    }
                };
                errorSaving.show(getFragmentManager(),"error saving note");
            }
        }
    };
    private void saveNote(){
        DatabaseConnector dbc = new DatabaseConnector(getActivity());
        if (noteInfoBundle==null){
            rowID = dbc.insertNotes(editText_Name.getText().toString(),editText_Describe.getText().toString(),false,1);
            //TODO:
        }else {
            dbc.updateNotes(rowID,editText_Name.getText().toString(),editText_Describe.getText().toString(),false,1);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        new LoadNoteTask().execute(rowID);
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putLong(MainActivity.ROW_ID,rowID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_menu_addedit,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_edit:
            {
                Bundle arguments = new Bundle();
                arguments.putLong(MainActivity.ROW_ID,rowID);
                arguments.putCharSequence("_name",editText_Name.getText());
                arguments.putCharSequence("_describe",editText_Describe.getText());
                listener.onEditNotes(arguments);
                return true;
            }
            case R.id.action_delete:
            {
                deleteNote();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteNote(){
        confirmDelete.show(getFragmentManager(),"confirm delete");

    }
    private DialogFragment confirmDelete = new DialogFragment(){
        @Override
        public Dialog onCreateDialog(Bundle bundle){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirm title");
            builder.setMessage("Confirm message");
            builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    final DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
                    AsyncTask<Long,Object,Object> deleteTask = new AsyncTask<Long, Object, Object>(){

                        @Override
                        protected Object doInBackground(Long... params) {
                            databaseConnector.deleteNote(params[0]);
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Object result){
                            listener.onNotesDeleted();
                        }
                    };
                deleteTask.execute(new Long[]{rowID});

                }
            });
            builder.setNegativeButton("Cancel",null);
            return builder.create();
        }
    };

    private class LoadNoteTask extends AsyncTask<Long,Object,Cursor>{
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();
            return databaseConnector.getOneNote(params[0]);
        }
        @Override
        protected void onPostExecute(Cursor result){
            super.onPostExecute(result);
            result.moveToFirst();
            int nameIndex = result.getColumnIndex("_name");
            int describeIndex = result.getColumnIndex("_describe");
            int dateAddIndex = result.getColumnIndex("_dateAdd");

            editText_Name.setText(result.getString(nameIndex));
            editText_Describe.setText(result.getString(describeIndex));
            editText_DateAdd.setText(result.getString(dateAddIndex));
            result.close();
            databaseConnector.close();
        }
    }







    public interface AddEditFragmentListener{
        void onNotesDeleted();
        void onEditNotes(Bundle arguments);
        void onAddEditCompleted(long rowID);

    }
}
