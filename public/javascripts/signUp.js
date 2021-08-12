function showSignUp(item1, item2, item3, item4, item5, item6, item7, item8) {
  document.getElementById(item1).style.display='block';
  document.getElementById(item2).style.display='block';
  document.getElementById(item3).style.display='block';

  document.getElementById(item4).style.display='none';
  document.getElementById(item5).style.display='none';
  document.getElementById(item6).style.display='none';
  document.getElementById(item7).style.display='none';
  document.getElementById(item8).style.display='none';
  return false;
}

function showCreateBlok(item1, item2, item3, item4, item5, item6) {
  document.getElementById(item1).style.display='block';
  document.getElementById(item2).style.display='block';
  document.getElementById(item3).style.display='block';

  document.getElementById(item4).style.display='none';
  document.getElementById(item5).style.display='none';
  document.getElementById(item6).style.display='none';
  return false;
}