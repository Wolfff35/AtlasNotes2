package ua.com.wolfff.atlasnotes2.Fragments;

import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import ua.com.wolfff.atlasnotes2.DatabaseConnector;
import ua.com.wolfff.atlasnotes2.R;

public class ListNotesFragment extends ListFragment {
    private ListView notesListView;
    private CursorAdapter notesAdapter;
    private ListNotesFragmentListener listener;

    public ListNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true); // Сохранение между изменениями конфигурации
        setHasOptionsMenu(true); // У фрагмента есть команды меню
        // Текст, отображаемый при отсутствии контактов
        //setEmptyText(getResources().getString(R.string.no_contacts));
        // Получение ссылки на ListView и настройка ListView
        notesListView = getListView();
        notesListView.setOnItemClickListener(viewNotesListener);
        notesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Имя контакта связывается с TextView в макете ListView
        String[] from = new String[] {"_name"};
        int[] to = new int[] { android.R.id.text1 };
        notesAdapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_1, null, from, to, 0);
        setListAdapter(notesAdapter); // Адаптер, поставляющий данные
    }

    // Обработка касания имени контакта в ListView
    AdapterView.OnItemClickListener viewNotesListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            listener.onNotesSelected(id); // Выбранный элемент передается
                                            // MainActivity
            }
    }; // Конец viewContactListener

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ListNotesFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }
    @Override
    public void onResume(){
        super.onResume();
        new GetNotesTask().execute((Object[]) null);
    }

    @Override
    public void onStop(){
        super.onStop();
        Cursor cursor = notesAdapter.getCursor();
        // Получение текущего курсора
        notesAdapter.changeCursor(null); // Адаптер не имеет курсора
        if (cursor != null)
            cursor.close();  // Освобождение ресурсов курсора
         super.onStop();
    }
    // Отображение команд меню фрагмента
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu_listnotes, menu);
    }
    // Обработка выбора команды из меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
                {
                    case R.id.action_add:
                listener.onAddNote();
                        return true;
                }
        return super.onOptionsItemSelected(item); // Вызов метода суперкласса 152
    }

    // Обновление набора данных
    public void updateNotesList(){
        new GetNotesTask().execute((Object[]) null);
    }
    // Конец класса ContactListFragment

    public interface ListNotesFragmentListener {
        void onNotesSelected(long rowID);

        // Отображение фрагмента AddEditFragment для добавления Note
        void onAddNote();
    }

    private class GetNotesTask  extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        // Открыть базу данных и вернуть курсор (Cursor) для всех контактов
        @Override
        protected Cursor doInBackground(Object... params) {
            databaseConnector.open();
            return databaseConnector.getAllNotes();
        }
        // Использовать курсор, полученный от метода doInBackground
        @Override
        protected void onPostExecute(Cursor result){
            notesAdapter.changeCursor(result); // Назначение курсора
                                                // для адаптера
            databaseConnector.close();
        }
    }
}

