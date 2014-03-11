package org.powerbot.script.rs3;

import java.util.EnumSet;

import org.powerbot.bot.rs3.tools.Map;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;

public class LocalPath extends Path {
	private final Locatable destination;
	private Tile tile;
	private TilePath tilePath;

	private final Map map;

	public LocalPath(final ClientContext factory, final Map map, final Locatable destination) {
		super(factory);
		this.destination = destination;
		this.map = map;
	}

	@Override
	public boolean traverse(final EnumSet<TraversalOption> options) {
		return isValid() && tilePath.traverse(options);
	}

	@Override
	public boolean isValid() {
		Tile end = destination.tile();
		if (end == null || end == Tile.NIL) {
			return false;
		}
		if (end.equals(tile) && tilePath != null) {
			return true;
		}
		tile = end;
		Tile start = ctx.players.local().tile();
		final Tile base = ctx.game.getMapBase();
		if (base == Tile.NIL || start == Tile.NIL || end == Tile.NIL) {
			return false;
		}
		start = start.derive(-base.x(), -base.y());
		end = end.derive(-base.x(), -base.y());
		final Map.Node[] path = map.getPath(start.x(), start.y(), end.x(), end.y(), ctx.game.getPlane());
		if (path.length > 0) {
			final Tile[] arr = new Tile[path.length];
			for (int i = 0; i < path.length; i++) {
				arr[i] = base.derive(path[i].x, path[i].y);
			}
			tilePath = ctx.movement.newTilePath(arr);
			return true;
		}
		return false;
	}

	@Override
	public Tile getNext() {
		return isValid() ? tilePath.getNext() : Tile.NIL;
	}

	@Override
	public Tile getStart() {
		return Tile.NIL;
	}

	@Override
	public Tile getEnd() {
		return destination.tile();
	}
}
