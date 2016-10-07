package com.simpleentity.serialize2.integration;

import org.junit.Before;
import org.junit.Test;

import com.simpleentity.testmodel.Album;
import com.simpleentity.testmodel.Artist;
import com.simpleentity.testmodel.Date;
import com.simpleentity.testmodel.Duration;
import com.simpleentity.testmodel.Sex;
import com.simpleentity.testmodel.Song;

public class AssetSerializationTest extends AbstractSerializationTest {

	private Artist artistCrazy;

	@Before
	public void setUp() {
		artistCrazy = Artist.newBuilder()
			.setName("Crazy Foo")
			.setSex(Sex.MALE)
			.setBirthDate(new Date(1967, 5, 17))
			.build(idFactory);
	}

	@Test
	public void testSerializeArtist() {
		serializeAndDeserialize(artistCrazy);
	}

	@Test
	public void testSerializeAlbum() {
		Album album = Album.newBuilder()
			.addSong(new Song("Intro", new Duration(4, 15000)))
			.addSong(new Song("Let's make music", new Duration(104, 84187100)))
			.setArtist(artistCrazy.getEntityId())
			.setTitle("The normal songs")
			.setReleaseDate(new Date(2019, 12, 31))
			.build(idFactory);
		serializeAndDeserialize(album);
	}


}
