package com.worthwhilegames.cardgames.shared.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.worthwhilegames.cardgames.R;
import com.worthwhilegames.cardgames.shared.CardGame;
import com.worthwhilegames.cardgames.shared.GameFactory;

/**
 * This activity will display the Game Rules
 */
public class RulesActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information);

		// Update the title to "Rules"
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.rulesActivityTitle);

		// Update the body text to the Rules
		TextView body = (TextView) findViewById(R.id.informationBody);

		// Set the rules for the Crazy Eight game type
		if (GameFactory.getGameType(this) == CardGame.CRAZY_EIGHTS) {
			body.setText(R.string.crazy_eight_game_rules_text);
		}
	}
}
