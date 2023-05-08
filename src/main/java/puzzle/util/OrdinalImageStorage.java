package puzzle.util;

import javafx.scene.image.Image;

public class OrdinalImageStorage implements ImageStorage<Integer> {

    private Image[] images;

    public OrdinalImageStorage(String path, String... filenames) {
        images = new Image[filenames.length];
        for (var i = 0; i < filenames.length; i++) {
            var url = String.format("%s/%s", path, filenames[i]);
            try {
                images[i] = new Image(url);
            } catch (Exception e) {
                // Failed to load image
            }
        }
    }

    @Override
    public Image get(Integer key) {
        return images[key];
    }

}
