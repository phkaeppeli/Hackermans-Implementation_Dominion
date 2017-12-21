package client.cardSelVC;

import shared.abstractClasses.Controller;

import java.util.ArrayList;
import client.cardSelVC.CardSelectionView;
import client.modelClasses.MenuModel;
import client.resourceClasses.FieldFX;
import client.soundeffects.Sound;

/**
 * Controller for CardSelection MVC
 * 
 * @author Philipp Lehmann
 *
 */
public class CardSelectionController extends Controller<MenuModel, CardSelectionView> {
	/**
	 * CONSTRUCTOR
	 * 
	 * @author Philipp Lehmann
	 * @param model
	 * @param view
	 *
	 */
	public CardSelectionController(MenuModel model, CardSelectionView view) {
		super(model, view);
		// Add ActionHandler for back button
		view.btnClose.setOnAction((event) -> {
			Sound.playButton();
			view.stop();
		});
		
		selectCards();
	}
	
	/**
	 * Selected Cards are saved into array and ID changed for CSS formatting.
	 * Then, the array are given to the MenuModel class by using a setter.
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	public void selectCards(){
		ArrayList<String> selActionCards = new ArrayList<String>();
		ArrayList<String> tempSelActionCards = new ArrayList<String>();
		
		for (FieldFX f : view.availActionCards) {
			// Event: Clicked
			f.setOnMouseClicked((event) -> {
				if(model.getSelActionCards() == null){
					selActionCards.add(f.name);
					f.setId("selectedCard");
				}else{
					for(String s : model.getSelActionCards()){
						tempSelActionCards.add(s);
					}
					
					if(tempSelActionCards.contains(f.name)){
						selActionCards.remove(f.name);
						f.setId("fieldFX");
					}else{
						selActionCards.add(f.name);
						f.setId("selectedCard");
					}
				}

				tempSelActionCards.clear();
				String[] cards = new String[selActionCards.size()];
				for(int i = 0; i < selActionCards.size(); i++){
					cards[i] = selActionCards.get(i);
				}				

				model.setSelActionCards(cards);
			});
		}
	}
}
