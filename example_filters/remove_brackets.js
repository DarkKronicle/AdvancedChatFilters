/*
Removes player name brackets smartly. If there isn't a color for the name, a new one is set.
By default that color is gray.

<DarkKronicle> -> DarkKronicle:
*/

function setup(payload) {
   payload.setAuthor('DarkKronicle')
   payload.setId('remove_brackets')
   payload.setDisplayName('Remove Brackets')
   payload.setHoverLines("Remove's the <>'s from player names\n\n§7<DarkKronicle> §f->§7 DarkKronicle:")
}


// Remove's brackets from player names using *magic*
function filter(payload) {
    var searchResult = payload.getSearchResult('regex', '^<([A-Za-z0-9_§]{3,16})>')
    if (searchResult.size() == 0) {
        // No match no go
        return
    }
    var format = '{}:'
    // Only ever one match
    // Fetch results
    var matchOne = searchResult.getMatches().get(0)
    // Text of the match
    var match = matchOne.match

    var replaceBuilder = payload.getNewReplaceBuilder()
    var replace = format.replace('{}', match.substring(1, match.length - 1))
    var style = payload.getStyleAt(matchOne.start + 1)
    var textBuilder = payload.getNewTextBuilder(replace).setStyle(style)
    if (payload.getColor(style) === null) {
        textBuilder.setColor(0xAAAAAA)
    }
    replaceBuilder.addReplacement(matchOne, textBuilder.build())
    payload.setText(replaceBuilder.build(payload.getText()))
}