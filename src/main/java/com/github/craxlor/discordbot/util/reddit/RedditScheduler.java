package com.github.craxlor.discordbot.util.reddit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.craxlor.discordbot.database.entity.RedditTask;
import com.github.craxlor.jReddit.Listings;
import com.github.craxlor.jReddit.Reddit;
import com.github.craxlor.jReddit.RedditPost;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class RedditScheduler {

    private Logger logger;
    private Guild guild;
    private Reddit reddit;
    private List<MyTimer> timerList;

    public RedditScheduler(Guild guild) {
        this.guild = guild;
        logger = LoggerFactory.getLogger("reddit");
        timerList = new ArrayList<MyTimer>();
        try {
            reddit = new Reddit();
        } catch (ParseException | IOException e) {
            logger.warn(e.getLocalizedMessage());
        }
    }

    public void schedule(RedditTask redditTask) {
        Date date = Calendar.getInstance().getTime();
        switch (redditTask.getFirstTime()) {
            case "HOUR" -> date = nextFullHour();
            case "MINUTE" -> date = nextFullMinute();
        }
        // create task
        MyTimerTask galleryTask = new MyTimerTask(redditTask);
        // create timer
        MyTimer galleryTimer = new MyTimer(
                guild.getName() + "-Reddit-" + (timerList.size() + 1) + "-Thread",
                true,
                redditTask);

        galleryTimer.scheduleAtFixedRate(galleryTask, date, redditTask.getPeriod());
        timerList.add(galleryTimer);
        logger.info("""
                scheduled a redditTask
                guild:  %s""".formatted(guild.getName()));
    }

    public void schedule(List<RedditTask> redditTasks) {
        for (RedditTask redditTask : redditTasks) {
            schedule(redditTask);
        }
    }

    public void stop(RedditTask redditTask) {
        for (MyTimer timer : timerList) {
            if (timer.getRedditTask().equals(redditTask)) {
                timer.cancel();
                timerList.remove(timer);
                logger.info("""
                        stopped a redditTask
                        guild:  %s""".formatted(guild.getName()));
                break;
            }
        }
    }

    public int size() {
        if (timerList == null)
            return -1;
        return timerList.size();
    }

    /**
     * Next full hour.
     *
     * @return Date today's next full hour
     */
    private Date nextFullHour() {
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY) + 1;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Next full minute.
     *
     * @return the date
     */
    private Date nextFullMinute() {
        final Calendar calendar = Calendar.getInstance();
        final int minute = calendar.get(Calendar.MINUTE) + 1;
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // ------------------------------------------------------------------------------------------------
    private class MyTimerTask extends TimerTask {
        private RedditTask redditTask;
        private TextChannel textChannel;
        private ArrayList<RedditPost> posted;

        MyTimerTask(RedditTask redditTask) {
            this.redditTask = redditTask;
            textChannel = guild.getTextChannelById(redditTask.getChannel_id());
            posted = new ArrayList<>();
        }

        @Override
        public void run() {
            boolean isPostCompatible = false;
            RedditPost redditPost = null;
            // get 500 posts
            List<RedditPost> listing = reddit.getListing(redditTask.getSubreddit(), Listings.HOT, 500);
            while (isPostCompatible == false) {
                // select a random post
                redditPost = listing.get(new Random().nextInt(listing.size()));
                // check if post has been used already
                if (posted.contains(redditPost))
                    continue;
                isPostCompatible = RedditHelper.canBePosted(redditPost);
            }
            // send message
            RedditHelper.sendRedditPost(textChannel, redditPost);
            if (posted.size() > 1000)
                posted.clear();

            posted.add(redditPost);
            // logger.info("posted " + redditPost.getUrl_overridden_by_dest());
        }

    }

    // ------------------------------------------------------------------------------------------------
    private class MyTimer extends Timer {
        private RedditTask redditTask;

        MyTimer(String arg0, boolean arg1, RedditTask redditTask) {
            super(arg0, arg1);
            this.redditTask = redditTask;
        }

        public RedditTask getRedditTask() {
            return redditTask;
        }
    }
}
