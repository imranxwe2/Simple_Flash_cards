package application.backend;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SpacedRepetitionService {

    // 🔥 Build study queue
    public static List<Card> buildStudyQueue(int deckId) {

        List<Card> all = CardDAO.getCardsByDeck(deckId);
        List<Card> queue = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        int index = 0;

        for (Card c : all) {

            // 🔴 Score 4 → time-based
            if (c.score == 4) {

                if (c.nextReview == null || c.nextReview.isEmpty()) {
                    continue;
                }

                LocalDateTime next = LocalDateTime.parse(c.nextReview);

                if (next.isBefore(now)) {
                    queue.add(c);
                }

                continue;
            }

            // 🟡 Session-based logic
            int frequency = getFrequency(c.score);
		
            if (index % frequency == 0) {
                queue.add(c);
            }

            index++;
        }

        return queue;
    }

    // 🔥 Update after answering
    public static void updateCard(Card c, int newScore) {

        c.score = newScore;

        if (newScore == 4) {
            c.completed = true;
            c.nextReview = LocalDateTime.now().plusHours(48).toString();
        } else {
            c.completed = false;
            c.nextReview = null;
        }

        CardDAO.updateReviewData(
                c.id,
                c.score,
                c.completed,
                c.nextReview
        );
    }

    // 🔧 helper
    private static int getFrequency(int score) {

        switch (score) {
            case 0: return 1;
            case 1: return 3;
            case 2: return 6;
            case 3: return 10;
            default: return 1;
        }
    }
	}

/*
 * score	case matched	result
0	case 0	1
1	case 1	3
2	case 2	6
3	case 3	10
anything else	default	1 */

/* | Score | Behavior |
| ---  | --- |
| 0 | show every cycle |
| 1 | show every 3 cards |
| 2 | show every 6 cards |
| 3 | show every 10 cards |
| 4 | ✅ ONLY if perfect → show after 48 hours | */

// helper returns score based on case
//else it is lower than 4 going to repeat in specfic freq

// Update the cards after answering the answer in study now page
// returns a ordered list of cards based on score, and 
