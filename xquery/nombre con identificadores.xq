for $player in /jornada/game/team/player
group by $name := $player/@name/string()
return element persons {
  attribute name { $name },
  attribute team { $player/../team/@team_code/string() },
  $player/@player_code ! element player_code { data() }
}
