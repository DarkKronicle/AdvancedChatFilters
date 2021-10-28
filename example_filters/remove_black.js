/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
function setup(payload) {
	payload.setAuthor('DarkKronicle')
	payload.setId('remove_black')
	payload.setDisplayName('Remove Color Black')
	payload.setHoverLines("Replace's the color black with a dark gray.")
}


function filter(payload) {
	// Create new text to reconstruct from
	var newText = payload.getNewTextBuilder().build();
	var textSize = payload.getText().getRawTexts().size()
	for (var i = 0; i < textSize; i++) {
		// Iterate through each
		var text = payload.getText().getRawTexts().get(i)
		var color = payload.getColor(text.getStyle())
		if (color != null && color.color() == 0) {
			// If it's black change the color and append
			var textBuilder = payload.getNewTextBuilder(text.getString()).setStyle(text.getStyle())
			textBuilder.setColor(0x666666)
			newText.append(textBuilder.build())
		} else {
			newText.append(text)
		}
	}
	// Build it and set it
	payload.setText(newText)
}
