import { describe, expect, it } from 'vitest';
import { asArray } from './models';

describe('asArray', () => {
  it('returns arrays directly', () => {
    expect(asArray<number>([1, 2])).toEqual([1, 2]);
  });

  it('unwraps common paginated API response shapes', () => {
    expect(asArray<{ id: number }>({ content: [{ id: 7 }] })).toEqual([{ id: 7 }]);
    expect(asArray<{ id: number }>({ data: [{ id: 8 }] })).toEqual([{ id: 8 }]);
  });

  it('returns an empty list for absent or invalid response data', () => {
    expect(asArray(null)).toEqual([]);
    expect(asArray({ content: 'not a list' })).toEqual([]);
  });
});
