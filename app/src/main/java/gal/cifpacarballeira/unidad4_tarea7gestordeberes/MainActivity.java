package gal.cifpacarballeira.unidad4_tarea7gestordeberes;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HomeworkAdapter adapter;
    private HomeworksViewModel homeworkList;
    private HomeworkRepository homeworkRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        homeworkRepository = new HomeworkRepository(this);
        homeworkList = new ViewModelProvider(this).get(HomeworksViewModel.class);
        List<Homework> homeworksFromDb = homeworkRepository.getAllHomework();
        if(homeworksFromDb.size() > 0){
            homeworkList.setHomeworks(homeworksFromDb);
        }
        // Inicialización de componentes
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);
        System.out.println("fsadaw");
        // Crear y configurar el adaptador
        System.out.println(homeworkList.getHomeworks().getValue().size());
        homeworkList.getHomeworks().observe(this, homeworkList -> {
                adapter = new HomeworkAdapter(homeworkList, homework -> showBottomSheet(homework));
            recyclerView.setAdapter(adapter);
        });

        // Este código sería lo mismo que la anterior línea
        // adapter = new HomeworkAdapter(homeworkList, this::showBottomSheet);
        // ¿Por qué le paso ese segundo parámetro?
        // Porque le estoy pasando la función que quiero que se lance al hacer click en un elemento
        // Investiga sobre "operador de referencia de método en Java"


        // Configuración del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        System.out.println("recycler");

        // Configuración del botón flotante
        fab.setOnClickListener(v -> showAddHomeworkDialog(null));
    }

    private void showAddHomeworkDialog(Homework homeworkToEdit) {

        NewHomeworkDialogFragment dialog = new NewHomeworkDialogFragment();
        // Pasarle el objeto Homework al diálogo si se está editando
        if (homeworkToEdit != null) {
            Bundle args = new Bundle();
            args.putParcelable("homework", homeworkToEdit);
            dialog.setArguments(args);
        }
        dialog.setOnHomeworkSavedListener(homework -> {
                    List<Homework> homeworks = homeworkList.getHomeworks().getValue();
                    if (homeworkToEdit == null) {
                        System.out.println(homeworks.size());
                        homeworks.add(homework);
                        System.out.println(homeworks.size());
                        homeworkRepository.insertHomework(homework);
                        System.out.println(homeworkRepository.getAllHomework().size());
                    } else {
                        homeworkRepository.updateHomework(homeworkToEdit.getId(), homework);
                        homeworks.set(homeworks.indexOf(homeworkToEdit), homework);
                    }
            homeworkList.setHomeworks(homeworks);
            adapter.notifyDataSetChanged();
                });
        dialog.show(getSupportFragmentManager(), "AddHomeworkDialog");
//
//        AddHomeworkDialogFragment dialog = AddHomeworkDialogFragment.newInstance(homeworkToEdit);
//        dialog.setOnHomeworkSavedListener(homework -> {
//            if (homeworkToEdit == null) {
//                homeworkList.add(homework);
//            } else {
//                homeworkList.set(homeworkList.indexOf(homeworkToEdit), homework);
//            }
//            adapter.notifyDataSetChanged();
//        });
//        dialog.show(getSupportFragmentManager(), "AddHomeworkDialog");
    }

    private void showBottomSheet(Homework homework) {
        // Creación del diálogo
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Inflar el layout del diálogo
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_homework_options, null);

        // Asignar acciones a los botones

        // Opción de editar
        view.findViewById(R.id.editOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showAddHomeworkDialog(homework);
        });

        // Opción de eliminar
        view.findViewById(R.id.deleteOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDeleteConfirmation(homework);
        });


        // Opción de marcar como completada
        view.findViewById(R.id.completeOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            homework.setCompleted(true);
            homeworkRepository.updateHomework(homework.getId(), homework);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Tarea marcada como completada", Toast.LENGTH_SHORT).show();
        });

        // Mostrar el diálogo
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void showDeleteConfirmation(Homework homework) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este deber?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    List<Homework> homeworks = homeworkList.getHomeworks().getValue();
                    homeworks.remove(homework);
                    homeworkList.setHomeworks(homeworks);
                    homeworkRepository.deleteHomework(homework.getId());
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
