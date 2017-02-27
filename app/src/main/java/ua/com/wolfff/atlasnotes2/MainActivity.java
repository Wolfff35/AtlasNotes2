package ua.com.wolfff.atlasnotes2;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ua.com.wolfff.atlasnotes2.Fragments.AddEditFragment;
import ua.com.wolfff.atlasnotes2.Fragments.ListNotesFragment;

public class MainActivity extends AppCompatActivity implements ListNotesFragment.ListNotesFragmentListener, AddEditFragment.AddEditFragmentListener{
    // Ключи идентификатора строки в объекте Bundle, передаваемом фрагменту
    public static final String ROW_ID = "row_id";

    Toolbar toolbar;
    FloatingActionButton fab;
    ListNotesFragment listNotesFragment;
    AddEditFragment addEditFragment;
    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        listNotesFragment = new ListNotesFragment();
        addEditFragment = new AddEditFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.containerMain,listNotesFragment);
        transaction.commit();

//        DatabaseConnector dbc = new DatabaseConnector(getApplicationContext());
//        dbc.insertNotes("name 1","describe 1",false,1);
//        dbc.insertNotes("name 2","describe 2",false,2);
//        dbc.insertNotes("name 3","describe 3",false,3);
//        dbc.insertNotes("name 4","describe 4",false,4);
//        dbc.insertNotes("name 5","describe 5",false,1);
//        dbc.insertNotes("name 6","describe 6",false,2);
//        dbc.insertNotes("name 7","describe 7",false,3);
//        dbc.insertNotes("name 8","describe 8",false,4);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listNotesFragment == null)
        {
            listNotesFragment =
                    (ListNotesFragment) getFragmentManager().findFragmentById(
                            R.id.listNotesFragment);
        }
    }
    public void onNoteSelected(long rowID){

    }

    @Override
    public void onNotesSelected(long rowID) {
        if (findViewById(R.id.containerMain) != null)
            displayNotes(rowID, R.id.containerMain);
    }
    private void displayNotes(long rowID, int viewID){
        addEditFragment = new AddEditFragment();
        // Передача rowID в аргументе DetailsFragment
        Bundle arguments = new Bundle();
        arguments.putLong(ROW_ID, rowID);
        addEditFragment.setArguments(arguments);
        // Использование FragmentTransaction для отображения AddEditFragment
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Отображение фрагмента AddEditFragment для добавления Note
    @Override
    public void onAddNote(){
        if (findViewById(R.id.containerMain) != null)
            displayAddEditFragment(R.id.containerMain, null);
    }

    // Отображение фрагмента для изменения или добавления Note
    private void displayAddEditFragment(int viewID, Bundle arguments){
        addEditFragment = new AddEditFragment();
        if (arguments != null) // Редактирование существующего notes
        addEditFragment.setArguments(arguments);
        // Использование FragmentTransaction для отображения AddEditFragment
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //Возврат к списку Notes после удаления
    @Override
    public void onNotesDeleted() {
        getFragmentManager().popBackStack();
        // Извлекает верхний элемент
        // из стека
    }

    @Override
    public void onEditNotes(Bundle arguments){
        //if (findViewById(R.id.fragmentContainer) != null)
        displayAddEditFragment(R.id.containerMain, arguments);
    }
    @Override
    public void onAddEditCompleted(long rowID){
        getFragmentManager().popBackStack();
        // Извлечение из стека
    }
}

