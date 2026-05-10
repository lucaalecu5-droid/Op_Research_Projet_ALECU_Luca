# Projet Recherche Operationnelle (Flots)
(Projet réalisé par ALECU Luca)
Ce projet implemente :
- Flot maximal + min-cut (Ford-Fulkerson / Edmonds-Karp).
- Flot de cout minimal (chemins augmentants) avec :
  - Bellman-Ford (couts negatifs possibles),
  - Dijkstra + potentiels (renormalisation des couts).
- Detection de cycles negatifs.
- Export des resultats au format GraphViz (.gv).
- des exemples de résultat sont disponibles dans le dossier gallerie

## Prerequis
- Java 8+ (javac/java dans le PATH).
- GraphViz (pour convertir les .gv en PDF si besoin).
  - Commande : `dot fichier.gv -Tpdf > res.pdf`
  - Important : utiliser l'invite de commande (pas PowerShell).

## Structure du projet
- `Main.java` : point d'entree, exemples et execution des algorithmes.
- `Graph.java` : graphe residuel et algorithmes.
- `Arrete.java` : representation d'un arc.
- `GraphVizWriter.java` : export GraphViz.
- Fichiers d'entree :
  - `graph_data.txt` : exemple simple au format impose.
  - `mincost_flow_ts.txt`, `mincost_flow_04.txt` : exemples min-cost flow.
- Fichiers GraphViz generes : `*.gv` (ex : `example1_maxflow.gv`).

## Compilation
Dans le dossier du projet :

```
javac *.java
```

## Lancer le programme (toutes les commandes)
### 1) Mode par defaut
```
java Main
```
Lance `graph_data.txt` si present avec la commande de maxflow, puis résoud les exemples integres (ceux demandés dans le cours (ils sot hardcodés et n'ont pas de fichiers d'entrée)

### 2) Flot max + min-cut sur un fichier
```
java Main maxflow <fichier>
```
Exemple :
```
java Main maxflow graph_data.txt
```
donne le maxflow et la min cut du fichier représenté en entrée et génère le le graph nomdefichier_maxflow.gv qui peut être observé gace a: `dot fichier.gv -Tpdf > nomdevotrechoix.pdf`

### 3) Min-cost flow (Bellman-Ford)
```
java Main mincost-bf <fichier>
```
Exemple :
```
java Main mincost-bf mincost_flow_ts.txt
```
Calcule un flot de cout minimal avec Bellman-Ford (supporte les couts negatifs),
affiche les arcs avec flot > 0 et genere un fichier `nomdefichier_mincost_bf.gv`.

### 4) Min-cost flow (Dijkstra + potentiels)
```
java Main mincost-dij <fichier>
```
Exemple :
```
java Main mincost-dij mincost_flow_04.txt
```
Calcule un flot de cout minimal avec Dijkstra + potentiels (plus rapide),
affiche les arcs avec flot > 0 et genere un fichier `nomdefichier_mincost_dij.gv`.

### 5) Exemples integres uniquement
```
java Main examples
```
Lance les exemples du sujet et genere les fichiers :
- `example1_maxflow.gv`
- `example2_assignment_nocap.gv`
- `example2_assignment_cap2.gv`
- `example2_assignment_min1.gv`
- `example3_mincost_ts.gv`
- `example3_mincost_04.gv`
- `example3_vital_base.gv`

Signification de chaque fichier :
- `example1_maxflow.gv` : graphe de l'exemple 1 avec les flots du max-flow (0 -> 4).
- `example2_assignment_nocap.gv` : assignation min-cost sans capacite par tache.
- `example2_assignment_cap2.gv` : assignation min-cost avec capacite 2 par tache.
- `example2_assignment_min1.gv` : assignation min-cost avec contrainte "chaque tache prise au moins une fois".
- `example3_mincost_ts.gv` : min-cost flow calcule de t vers s sur le graphe de l'exemple 3.
- `example3_mincost_04.gv` : min-cost flow calcule de 0 vers 4 sur le meme graphe.
- `example3_vital_base.gv` : flot max de base sur (t -> s) avant la recherche de l'arc vital.

## Format des fichiers d'entree
Chaque graphe est defini par :

```
#nodes #arcs s t
u v capacite cout
u v capacite cout
...
```

- `#nodes` : nombre de noeuds (0..n-1)
- `#arcs` : nombre d'arcs
- `s` : source
- `t` : puits
- chaque ligne d'arc : `from to capacity cost`
- Les valeurs sont separees par des espaces.

## Fichiers .gv generes
A chaque execution, le programme ecrit un fichier GraphViz :
- `*_maxflow.gv` pour `maxflow`
- `*_mincost_bf.gv` pour `mincost-bf`
- `*_mincost_dij.gv` pour `mincost-dij`
- `example*.gv` pour les exemples integres

Les labels affichent :
- **flot/capacite** en vert
- **cout unitaire** en rouge
- Les noeuds `s` et `t` sont colories (source en vert, puits en bleu).

Conversion en PDF :
```
dot fichier.gv -Tpdf > res.pdf
```

## Exemples integres (rappel)
- **Exemple 1** : flot max sur un petit graphe.
- **Exemple 2** : assignation min-cost (10 personnes, 8 taches) :
  - sans capacite sur les taches
  - capacite 2 par tache
  - chaque tache prise au moins une fois
- **Exemple 3** : min-cost flow + arc vital sur le graphe du sujet.

## Conseils / Depannage
- Si `dot` est introuvable : installer GraphViz et verifier le PATH.
- Si les .gv sont vides, verifier que le fichier d'entree respecte le format.
- En cas d'erreur de cycle negatif, le graphe contient un cycle de cout total negatif.
- Lancer les commandes depuis le dossier du projet pour trouver les fichiers.

## Auteurs / Contact
Projet realise pour le cours de Recherche Operationnelle.

