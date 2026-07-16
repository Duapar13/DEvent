# DEvent

**Événements planifiés côté serveur.** Annonces récurrentes (rappel de
tournoi...) et "loot-drop" : un objet rare mis en vente automatiquement à
l'hôtel des ventes (DAuction) pour que les joueurs se précipitent dessus.
Comme DChat, aucune donnée à persister : les événements sont définis dans
`config.yml` et leurs minuteries repartent de zéro à chaque démarrage.

## Fonctionnalités

- **Événements `broadcast`** : un message serveur, envoyé toutes les
  `interval-minutes`.
- **Événements `loot-drop`** : le même message, suivi d'un objet aléatoire
  de `loot-pool` mis en vente à l'hôtel des ventes (DAuction) pour un prix
  aléatoire entre `price-min` et `price-max`, vendeur "Serveur".
- **`/event list`** : liste les événements configurés et leur fréquence.
- **`/event trigger <id>`** (admin) : déclenche un événement immédiatement,
  utile pour tester une configuration sans attendre l'intervalle.
- **Catalogue défini dans `config.yml`**, comme les classes de DClass ou
  les rangs de DRank — pas de gestion en jeu.

## Intégration DAPI

DEvent **ne dépend pas** de DAPI (`softdepend: [DAPI]`) : les événements
`broadcast` marchent sans lui. Les événements `loot-drop` ont besoin de
`AuctionService` pour créer l'annonce ; sans DAuction installé, le message
est quand même diffusé, juste sans mise en vente (avertissement dans les
logs).

- **Consomme `AuctionService`** (DAuction, optionnel) : `createListing()`
  pour les événements `loot-drop`, via `AuctionLookup` isolé (même
  principe que `RankLookup`/`PunishLookup` de DChat) - vendeur "système"
  (UUID `00000000-0000-0000-0000-000000000000`, nom "Serveur").
- **Ne fournit aucun service** : DEvent est un orchestrateur, rien à
  exposer aux autres plugins - se contente de s'enregistrer auprès de DAPI
  (`/dapi list`).

## Commandes

| Commande | Description |
|---|---|
| `/event list` | Liste les événements planifiés et leur fréquence. |
| `/event trigger <id>` | Déclenche un événement immédiatement (admin). |

## Permissions

| Permission | Défaut | Description |
|---|---|---|
| `devent.use` | `true` | Lister les événements planifiés. |
| `devent.admin` | `op` | Déclencher un événement manuellement. |

## Configuration (`config.yml`)

```yaml
events:
  - id: butin-rare
    type: loot-drop
    interval-minutes: 120
    announce: '&6&lÉVÉNEMENT &e- Un objet rare apparaît à l''hôtel des ventes !'
    loot-pool: [DIAMOND_BLOCK, NETHERITE_INGOT, ENCHANTED_GOLDEN_APPLE]
    price-min: 500
    price-max: 2000
  - id: rappel-tournoi
    type: broadcast
    interval-minutes: 60
    announce: '&b&lTOURNOI &f- Prépare-toi !'
```

## Compiler le projet

Dépend de l'API Spigot 26.1.2 et, en `provided`, de DAPI (facultatif à
l'exécution) :

```
cd ../DAPI && mvn install
cd ../DEvent && mvn clean package
```

Pour que les événements `loot-drop` créent réellement une annonce,
installe aussi [DAuction](../DAuction). Voir
[`libs/README.md`](libs/README.md) pour la mise en place de l'API Spigot.

## Roadmap / idées d'extension

- Événements de type "tournoi" avec une vraie mécanique (zone d'arène,
  classement) plutôt qu'une simple annonce.
- Persister l'heure du prochain déclenchement pour survivre à un
  redémarrage (actuellement, l'intervalle repart de zéro à chaque boot).
- Loot-pool pondéré (objets plus ou moins rares).

## Licence

MIT — voir [`LICENSE`](LICENSE).
