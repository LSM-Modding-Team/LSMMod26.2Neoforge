"""Ordered pairs: first material is the front apron, second is the rest of the slab."""
import itertools
MATERIALS={'white':'classroom_white_wall','dark':'dark_school_wall','light':'light_school_wall','street':'street_school_wall'}
EXISTING_PAIRS={
    ('street','dark'):'supported_school_glass',
    ('dark','light'):'supported_school_glass_reversed',
    ('white','light'):'mixed_school_glass',
    ('street','white'):'mixed_school_glass_reversed',
}
NEW_PAIRS={pair:f'{pair[0]}_{pair[1]}_school_glass' for pair in itertools.permutations(MATERIALS,2) if pair not in EXISTING_PAIRS}
NEW_GLASS_IDS=tuple(NEW_PAIRS.values())
