/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
function setup(payload) {
	payload.setAuthor('DarkKronicle')
	payload.setId('doof')
	payload.setDisplayName('Dingus')
	payload.setHoverLines("Changes a word to dingus randomly")
}


function filter(payload) {
	var searchResult = payload.getSearchResult('regex', '\\w+');
	if (searchResult.size() == 0) {
		// No match no go
		return;
	};
	var replaceBuilder = payload.getNewReplaceBuilder()
	for (var i = 0; i < searchResult.size(); i++) {
		// Alter this to change how often it happens
		if (Math.random() < 0.1) {
			match = searchResult.getMatches().get(i);
			replaceBuilder.addReplacement(match, "dingus")
		}
	}
	payload.setText(replaceBuilder.build(payload.getText()));
}
