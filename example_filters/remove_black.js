function setup(payload) {
   payload.setAuthor('DarkKronicle')
   payload.setId('remove_black')
   payload.setDisplayName('Remove Color Black')
   payload.setHoverLines("Replace's the color black with a dark gray.")
}


// Remove's brackets from player names using *magic*
function filter(payload) {
    var newText = payload.getNewTextBuilder().build();
    var textSize = payload.getText().getRawTexts().size()
    for (var i = 0; i < textSize; i++) {
        var text = payload.getText().getRawTexts().get(i)
        var color = payload.getColor(text.getStyle())
        if (color != null && color.color() == 0) {
            var textBuilder = payload.getNewTextBuilder(text.getString()).setStyle(text.getStyle())
            textBuilder.setColor(0x666666)
            newText.append(textBuilder.build())
        } else {
            newText.append(text)
        }
    }
    payload.setText(newText)
}