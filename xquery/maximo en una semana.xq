for $player in /jornada[@nro='11']/game/team/player
order by $player/@point/number() descending
return element persons {
  attribute point { $player/@point },
  attribute name { $player/@name }
}