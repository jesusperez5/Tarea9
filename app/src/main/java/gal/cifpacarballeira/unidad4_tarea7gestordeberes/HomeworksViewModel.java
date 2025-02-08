package gal.cifpacarballeira.unidad4_tarea7gestordeberes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeworksViewModel extends ViewModel {
    private MutableLiveData<List<Homework>> homeworks;

    public HomeworksViewModel() {
        homeworks = new MutableLiveData<>(new ArrayList<>()); // Asegurar inicialización
    }

    public LiveData<List<Homework>> getHomeworks(){
        return homeworks;
    }

    public void setHomeworks(List<Homework> homeworks){
        this.homeworks.setValue(homeworks);
    }
}
