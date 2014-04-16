package imdb;

import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import perspectives.two_d.Viewer2D;

public class IMDBViewer extends Viewer2D{

	private IMDBDataSource data;
	public IMDBViewer(String name, IMDBDataSource data) {
		super(name);
		this.data = data;
		
		playWithData();
	}
	public void playWithData()
	{
		String search=JOptionPane.showInputDialog("Search a movie");
		
		ArrayList<CompactMovie> movieList = data.searchMovie(search);
		String msg="Shows 10 result:";
		System.out.println("Shows 10 result:");
		int i=0;
		for(CompactMovie m:movieList)
		{
			System.out.println(""+i+": "+m);
			msg+="\r\n"+i+": "+m;
			i++;
		}
		JOptionPane.showMessageDialog(null, msg);
		Random rand = new Random();
		int index=Integer.parseInt( JOptionPane.showInputDialog("Give a movie Index"));
		System.out.println("We choose a random index(0-9):"+index);
		Movie movie = this.data.getMovie(movieList.get(index));
		System.out.println("Movie:"+movie);
		JOptionPane.showMessageDialog(null, "Movie:"+movie);
		System.out.println("Lets look in to the actors:");
		msg="Lets look in to the actors:";
		i=0;
		for(CompactPerson p: movie.getActors())
		{
			System.out.println(""+i+": "+p);
			msg+="\r\n"+i+": "+p;
			i++;
			if(i==10) break;
		}
		JOptionPane.showMessageDialog(null, msg);
		msg="Lets look with another method!";
		System.out.println("Lets look with another method!");
		i=0;
		for(CompactPerson p: this.data.getActors(movie))
		{
			System.out.println(""+i+": "+p);
			i++;
			msg+="\r\n"+i+": "+p;
			if(i==10) break;
		}
		JOptionPane.showMessageDialog(null, msg);
		msg="Lets look with Director";
		System.out.println("Lets look with Director");
		i=0;
		for(CompactPerson p: this.data.getDirectors(movie))
		{
			System.out.println(""+i+": "+p);
			i++;
			msg+="\r\n"+i+": "+p;
			if(i==10) break;
		}
		JOptionPane.showMessageDialog(null, msg);
		System.out.println("Lets search persons!!");
		search=JOptionPane.showInputDialog("Lets search persons!!");
		msg="Searching Person: "+search;
		System.out.println("Searching Person: "+search);
		ArrayList<CompactPerson> personList = data.searchPerson(search);
		System.out.println("Shows 10 result:");
		i=0;
		for(CompactPerson p:personList)
		{
			System.out.println(""+i+": "+p);
			msg+="\r\n"+i+": "+p;
			i++;
		}
		JOptionPane.showMessageDialog(null, msg);
		Person person = this.data.getPerson(personList.get(0));
		msg="We are looking at: "+person+"\r\nActed movies:";
		System.out.println("We are looking at: "+person);
		System.out.println("Acted Movies");
		i=0;
		for(CompactMovie m: this.data.getActedMovieList(person))
		{
			System.out.println(""+i+": "+m);
			msg+="\r\n"+i+": "+m;
			i++;
			if(i==10) break;
		}
		JOptionPane.showMessageDialog(null, msg);
		System.out.println("Biography:"+this.data.getBiography(person).get(0));
		JOptionPane.showMessageDialog(null, "Biography:"+this.data.getBiography(person).get(0));
	}

	@Override
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}

}
