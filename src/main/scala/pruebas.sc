val bigNames = List("Alan Turing","Alonzo Church","John McCarthy","Haskell Curry")
val shortNames = bigNames.map(n => n.split(" ").last).filter(_.length < 7).sortBy(s => (s.length, s))