showCreateBlock

function showCreateBlock(item1) {
  document.getElementById(item1).style.display='block';
  return false;
}

splitLines();

window.onresize = showLines;

function showLines() {
    var lines = getLines();
    console.log(
    lines.map(function (line) {
        return line.map(function (span) {
            return span.innerText;
        }).join(' ')
    }));
}

function splitLines() {
    var p = document.getElementsByTagName('p')[0];
    p.innerHTML = p.innerText.split(/\s/).map(function (word) {
        return '<span>' + word + '</span>'
    }).join(' ');
}

function getLines() {
    var lines = [];
    var line;
    var p = document.getElementsByTagName('p')[0];
    var words = p.getElementsByTagName('span');
    var lastTop;
    for (var i = 0; i < words.length; i++) {
        var word = words[i];
        if (word.offsetTop != lastTop) {
            lastTop = word.offsetTop;
            line = [];
            lines.push(line);
        }
        line.push(word);
    }
    return lines;
}
