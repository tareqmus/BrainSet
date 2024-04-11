package com.brainset.ocr;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TipsActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private Runnable tipsRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.study_tips);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRepeatingTips();
    }

    private void startRepeatingTips() {
        final List<StudyTip> tips = generateTips();
        if (tipsRunnable == null) {
            tipsRunnable = new Runnable() {
                @Override
                public void run() {
                    showRandomTip(tips);
                    handler.postDelayed(this, 60000); // Repeat every 60 seconds
                }
            };
        }
        handler.post(tipsRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(tipsRunnable);
    }

    private List<StudyTip> generateTips() {
        List<StudyTip> tips = new ArrayList<>();
        tips.add(new StudyTip("Plan Your Day", "Use pictures or colors to make a fun plan for study, play, and chill time!"));
        tips.add(new StudyTip("Tiny Tasks", "Big homework? No problem! Break it into small pieces like a puzzle."));
        tips.add(new StudyTip("Colorful Helpers", "Use bright charts and lists to remember what to do next."));
        tips.add(new StudyTip("Quiet Zone", "Find a cozy spot with less noise and things around to do your best thinking."));
        tips.add(new StudyTip("Jump and Stretch", "Feel wiggly? Take quick breaks to hop or stretch between study times."));
        tips.add(new StudyTip("Race the Clock", "Use a timer to make a game of your study time. Can you beat the clock?"));
        tips.add(new StudyTip("Learn by Doing", "Build, draw, or act things out to learn in a fun way."));
        tips.add(new StudyTip("Goal Stars", "Set mini goals and give yourself a star each time you reach one."));
        tips.add(new StudyTip("Question Time", "Ask yourself, 'Am I focused?' to become a super smart detective of your own mind."));
        tips.add(new StudyTip("Cheer On", "Every bit of effort counts. Celebrate the small wins with fun rewards!"));
        tips.add(new StudyTip("Buddy Up", "Study with a friend or family member. Teaching someone else helps you learn too!"));
        tips.add(new StudyTip("Picture It", "Draw what you're learning. Making drawings can turn tricky ideas into fun pictures."));
        tips.add(new StudyTip("Memory Games", "Turn what you need to remember into a cool game or a catchy song."));
        tips.add(new StudyTip("Choose Your Own Adventure", "Pick which homework to do first. Starting with your favorite can be a fun beginning!"));
        tips.add(new StudyTip("Sticker Power", "Use stickers to mark your progress on tasks. The more stickers, the closer you are to done!"));
        tips.add(new StudyTip("Snack Breaks", "Healthy snacks can power up your brain. Make snack time a part of your study schedule."));
        tips.add(new StudyTip("Dress the Part", "Wear your favorite superhero cape or hat while studying. It can make you feel powerful and focused!"));
        tips.add(new StudyTip("Take Charge with Choices", "Whenever you can, choose how to do your tasks (like writing or typing). Feeling in control can make studying more fun."));
        tips.add(new StudyTip("Funny Timer Names", "Name your timers after superheroes or favorite animals. 'Study like a Cheetah' for quick tasks or 'March like an Elephant' for slow, thoughtful work."));
        tips.add(new StudyTip("High-Five Breaks", "Give yourself a high-five in the mirror during breaks. Tell yourself you’re doing great!"));
        tips.add(new StudyTip("Story Time", "Turn your study notes into a story or comic strip with characters and adventures."));
        tips.add(new StudyTip("Magic Words", "Create your own secret codes or magic words for tough concepts. It's like being a learning wizard!"));
        tips.add(new StudyTip("Puzzle Pieces", "Print information on cards and mix them up. Challenge yourself to put the puzzle back together."));
        tips.add(new StudyTip("Learning Potions", "Mix up a 'learning potion' drink (a healthy snack smoothie) to sip while studying."));
        tips.add(new StudyTip("Superhero Poses", "Strike a superhero pose before starting a task. It can boost your confidence and energy!"));
        tips.add(new StudyTip("Doodle Notes", "Use doodles and drawings in your notes. It’s fun and helps you remember better."));
        tips.add(new StudyTip("Dance Breaks", "Shake out the sillies with a dance break. A few minutes of dancing can help you focus better afterward."));
        tips.add(new StudyTip("Worry Monster", "Have a box or a toy monster where you can 'feed' your worries before you start studying, helping you clear your mind."));
        tips.add(new StudyTip("Space Explorer", "Pretend you're an astronaut studying the stars or planets when you're reading about science. Use your imagination to travel through space!"));
        tips.add(new StudyTip("Time Traveler Day", "Choose a different historical period each week. Dress up or decorate your study space like that era when working on history assignments."));
        tips.add(new StudyTip("Spell Caster’s Spell", "Create magical spells for memorizing formulas or spelling words. Wave a wand (or a pencil) as you recite them."));
        tips.add(new StudyTip("Inventor’s Workshop", "Turn a STEM project or problem into an invention challenge. Sketch and plan like a famous inventor."));
        tips.add(new StudyTip("Secret Agent Notes", "Write your notes as if they are secret messages. Use invisible ink pens or create your own code."));
        tips.add(new StudyTip("Echo Reading", "Read a paragraph, then 'echo' it back in your own words. It’s like having a conversation with the book!"));
        tips.add(new StudyTip("Hero’s Journey", "Turn your study topics into a hero’s journey story where you're the hero overcoming obstacles (quizzes and tests) with knowledge."));
        tips.add(new StudyTip("Puppet Show Review", "Use puppets to review your study material. Each puppet can be a character from your lessons."));
        tips.add(new StudyTip("Karaoke Knowledge", "Turn facts or information you need to remember into lyrics of a catchy tune. Sing your studies!"));
        tips.add(new StudyTip("Adventure Map", "Create a map of your study goals for the week. Each completed task leads you closer to the treasure."));

        return tips;
    }

    private void showRandomTip(List<StudyTip> tips) {
        int randomIndex = new Random().nextInt(tips.size());
        StudyTip randomTip = tips.get(randomIndex);
        Toast.makeText(this, randomTip.getTitle() + ": " + randomTip.getTip(), Toast.LENGTH_LONG).show();
    }
}
