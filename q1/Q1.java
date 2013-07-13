/**
 * General Strategy to find maximum number of concurrent team combinations.
 *
 * Given: Teams can have 1 or 2 members. Want: Maximum number of teams
 * containing unique members from a list of predefined teams.
 *
 * Strategy: - Cleanse list so that there are no duplicates (e.g. A & B is
 * equivalent to B & A).
 *
 * - Segregate list into teams with single membership, and others. The purpose
 * here is to benefit from the list of singles already being a subset of the max
 * solution.
 *
 * - Filter list of teams containing members from the singles list. We are now
 * left with a filtered list, usually smaller (but never bigger) that we can
 * perform the algorithm on.
 *
 * - Identify teams whose members appear only once. They will be included as
 * part of the max set, so no need to include them in the recursion sets, and no
 * reason to filter by them since their members are not in any other team.
 *
 * - Recursion Alert *** Should translate to iteration, but phffbbbbt. 
 * 
 * 1. Iterate through the list of filtered teams. Pivot on that team. 
 * 
 * 2. Create a filtered list by filtering out any team containing members from 
 * the picked team. 
 * 
 * 3. If there are no teams left after filtering, this branch contains 1
 * unique team: the picked (pivot) team. 
 * 
 * 4. If there is one team left after filtering, this branch contains 2 unique 
 * teams, the pivot, and the last team.
 * 
 * 5. Otherwise, recursion on the list of filtered teams. 
 * 
 * 6. Return the max value from recursion, + 1 (your pivot at this level).
 *
 * @author wolvertron
 */

import java.util.*;


public class Q1
{
  static final boolean DEBUG = false;
  static final boolean STATS = true;
  static final boolean DATA = true;
  static final String DEBUG_PREFIX = "[DEBUG]\t";
  static final String DEBUG_HR =
    "*************************************************************************";
  static long filterCalls = 0;
  static long pivotCalls = 0;
  static long cacheHit = 0;
  static long cacheMiss = 0;
  static HashMap pivotCache = new HashMap();

  public static void main(String[] args)
  {
    Scanner in = new Scanner(System.in);
    int nlines = Integer.parseInt(in.nextLine());
    int max = 0;
    String line;
    
    // NOTE:  Sets are used frequently here, because they have the nice property
    //        of only ever containing one of a thing. Kind of a free dedupe.
    Set teams = new HashSet();
    Set filtered;
    Set singles = new HashSet();
    Set uniques = new HashSet();
    Map<String, List> names = new HashMap();
    
    
    // Create unique list of teams, eliminating duplicates.
    // e.g. a & b, b & a
    // Also create a list of single person teams.
    // ALSO create a list containing how many times each name appears, and
    // where. An index, if you will.
    for (int i = 0; i < nlines; i++)
    {
      line = in.nextLine();

      // Split lines along & delimiters
      Set members = new HashSet(Arrays.asList(line.split("\\s*&\\s*")));

      // Create an index of which teams a name appears in.
      for (String name : (Set<String>) members)
      {
        if (!names.containsKey(name))
        {
          names.put(name, new ArrayList());
        }
        names.get(name).add(members);
      }

      // Split off singles, so we may later filter the non-singles by them.
      if (members.size() == 1)
      {
        singles.add(members);
      }
      else
      {
        teams.add(members);
      }
    }

    // Move teams having all unique members (occurring only once)
    // into uniques list.
    for (Map.Entry<String, List> entry : names.entrySet())
    {
      if (entry.getValue().size() == 1)
      {
        Set team = (Set) entry.getValue().get(0);

        boolean unique = true;
        // If any of the members of this team appear more than once,
        // the team isn't comprised of unique individuals.
        for (String member : (Set<String>) team)
        {
          if (names.get(member).size() > 1)
          {
            unique = false;
          }
        }
        if (unique)
        {
          if (team.size() == 1)
          {
            singles.remove(team);
          }
          else
          {
            teams.remove(team);
          }
          uniques.add(team);
        }
      }
    }

    // Filter remaining non-unique teams by remaining non-unique singles.
    filtered = filter(singles, teams);

    // Now I'm out of tricks to make the data set smaller before starting the
    // algorithm proper.
    // Well. I have one more.
    // TODO:  It is always advantageous to filter as much as you can as soon as
    //        you can. So, pivoting on teams whose members have a lot of
    //        references would eliminate other teams in the filter.
    //        So... creating an ordering by the sum of the reference count
    //        per team member, and using that as the weight for a
    //        priority queue would probably work out pretty well.    


    for (Set team : (Set<Set>)teams)
    {
      max = Math.max(max, pivot(team, filtered));
    }

    if (DATA)
    {
      System.out.println(DEBUG_PREFIX + "Uniques" 
        + "[" + uniques.size() + "]: " + uniques);
      System.out.println(DEBUG_PREFIX + "Non-unique Singles"
        + "[" + singles.size() + "]: " + singles);
      System.out.println(DEBUG_PREFIX + "Non-unique Teams"
        + "[" + teams.size() + "]: " + teams);
      System.out.println(DEBUG_PREFIX + "Non-uniques filtered by Singles" 
        + "[" + filtered.size() + "]: " + filtered);
      System.out.println(DEBUG_PREFIX 
        + "Unique combinations of remaining filtered teams: " + max);
    }

    if (STATS)
    {
      System.out.println(DEBUG_PREFIX + "Filter Calls: " + filterCalls);
      System.out.println(DEBUG_PREFIX + "Pivot Calls: " + pivotCalls);
      System.out.println(DEBUG_PREFIX + "Cache Hit: " + cacheHit);
      System.out.println(DEBUG_PREFIX + "Cache Miss: " + cacheMiss);
    }

    max += singles.size();
    max += uniques.size();

    System.out.println(max);
  }

  /**
   * Create a new set containing all sets except those elements containing
   * any of the elements in filters.
   *
   * @param filters
   * @param sets
   *
   * @return
   */
  private static Set filter(Set filters, Set<Set> sets)
  {
    filterCalls++;
    Set filtered = new HashSet();

    for (Set set : sets)
    {
      boolean valid = true;

      for (Object filter : filters)
      {
        // Given a set of filters, filter by each.
        if (filter instanceof Set)
        {
          for (Object filt : (Set) filter)
          {
            if (set.contains(filt))
            {
              valid = false;
            }
          }
        }
        else
        {
          // Otherwise, just filter.
          if (set.contains(filter))
          {
            valid = false;
          }
        }
      }
      if (valid)
      {
        filtered.add(set);
      }
    }

    if (DEBUG)
    {
      System.out.println(DEBUG_PREFIX + DEBUG_HR);
      System.out.println(DEBUG_PREFIX + "FILTER FUNCTION");
      System.out.println(DEBUG_PREFIX + DEBUG_HR);
      System.out.println(DEBUG_PREFIX + "Filters: " + filters);
      System.out.println(DEBUG_PREFIX + "Sets: " + sets);
      System.out.println(DEBUG_PREFIX + "Filtered: " + filtered);
      System.out.println(DEBUG_PREFIX + DEBUG_HR);
    }
    return filtered;
  }

  private static int pivot(Set pivot, Set<Set> teams)
  {
    boolean hit = false;
    Set<Set> filtered = null;

    pivotCalls++;
    int value;

    if (teams.isEmpty())
    {
      value = 0;
    }
    else
    {
      // filter pivot team members from remaining teams.
      filtered = filter(pivot, teams);

      //String key = pivot.toString() + teams.toString();
      String key = filtered.toString();
      if (pivotCache.containsKey(key))
      {
        hit = true;
        cacheHit++;
        value = (Integer) pivotCache.get(key);
      }
      else
      {
        cacheMiss++;
        if (filtered.isEmpty())
        {
          // No options remain, just the pivot.
          value = 1;
        }
        else if (filtered.size() == 1)
        {
          // Only one option left here, plus pivot
          value = 2;
        }
        else
        {
          // pivot through teams, finding path yielding largest
          // number of teams.
          int max = 0;
          for (Set team : filtered)
          {
            max = Math.max(max, pivot(team, filtered));
          }
          value = max + 1;
        }
        pivotCache.put(key, value);
      }
    }

    if (DEBUG)
    {
      System.out.println(DEBUG_PREFIX + DEBUG_HR);
      System.out.println(DEBUG_PREFIX + "PIVOT FUNCTION"
        + (hit ? "\tCACHE HIT!\t" + cacheHit : ""));
      System.out.println(DEBUG_PREFIX + DEBUG_HR);

      System.out.println(DEBUG_PREFIX + "Pivot: " + pivot);
      System.out.println(DEBUG_PREFIX + "Teams: " + teams);
      System.out.println(DEBUG_PREFIX + "Filtered: " + filtered);
      System.out.println(DEBUG_PREFIX + "Value: " + value);

      System.out.println(DEBUG_PREFIX + DEBUG_HR);
    }

    return value;
  }
}
