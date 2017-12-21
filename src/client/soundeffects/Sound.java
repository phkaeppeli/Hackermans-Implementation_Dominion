package client.soundeffects;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * 
 * @author Philipp Lehmann
 *
 */

public class Sound {
	public static boolean playButtonSound = false;
	public static boolean playBackgroundSound = false;
	public static MediaPlayer player;

	/**
	 * play button sound
	 */
	public static void playButton() {
		if (playButtonSound) {
			String path_btn_sound = "src/client/soundeffects/button_sound.mp3";
			new MediaPlayer(new Media(new File(path_btn_sound).toURI().toString())).play();
		}
	}

	/**
	 * play & repeat Song
	 */
	public static void playSong() {
		if (playBackgroundSound) {
			// Blizzard Entertainment - The Arena Awaits, https://playhearthstone.com/en-us/media/
			String path_background_music = "src/client/soundeffects/the-arena-awaits.mp3";
			player = new MediaPlayer(new Media(new File(path_background_music).toURI().toString()));
			player.setAutoPlay(true);
			player.setCycleCount(MediaPlayer.INDEFINITE);
			player.seek(Duration.ZERO);
			player.play();
		}
	}
}
