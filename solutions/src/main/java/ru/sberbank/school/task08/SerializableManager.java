package ru.sberbank.school.task08;

import lombok.NonNull;
import ru.sberbank.school.task08.state.*;
import ru.sberbank.school.util.Solution;

import java.io.*;
import java.util.List;

@Solution(8)
public class SerializableManager extends SaveGameManager<MapState<GameObject>, GameObject> {
    /**
     * Конструктор не меняйте.
     */
    public SerializableManager(@NonNull String filesDirectoryPath) {
        super(filesDirectoryPath);
    }

    @Override
    public void initialize() {
//        throw new UnsupportedOperationException("Implement me!");
    }

    @Override
    public void saveGame(@NonNull String filename,
                         @NonNull MapState<GameObject> gameState) throws SaveGameException {
        String path = getPath(filename);

        try (FileOutputStream fos = new FileOutputStream(path);
                ObjectOutputStream out = new ObjectOutputStream(fos)) {

            out.writeObject(gameState);
        } catch (IOException | NullPointerException e) {
            throw new SaveGameException("SerializableManager saving error",
                    e.getCause(), SaveGameException.Type.IO, gameState);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public MapState<GameObject> loadGame(@NonNull String filename) throws SaveGameException {
        String path = getPath(filename);

        try (FileInputStream fis = new FileInputStream(path);
                ObjectInputStream in = new ObjectInputStream(fis)) {

            return (MapState<GameObject>) in.readObject();
        } catch (IOException | NullPointerException | ClassNotFoundException e) {
            throw new SaveGameException("SerializableManager loading error",
                    e.getCause(), SaveGameException.Type.IO, null);
        }
    }

    @Override
    public InstantiatableEntity createEntity(InstantiatableEntity.Type type,
                                             InstantiatableEntity.Status status,
                                             long hitPoints) {
        return new GameObject(type, status, hitPoints);
    }

    @Override
    public MapState<GameObject> createSavable(String name, List<GameObject> entities) {
        return new MapState<>(name, entities);
    }

    private String getPath(String filename) {
        return filesDirectory + File.separator + filename;
    }
}
